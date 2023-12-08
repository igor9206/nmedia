package ru.netology.nmedia.auth

import android.content.Context
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.worker.SendPushTokenWorker
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"

    private val _authState = MutableStateFlow<AuthState>(
        AuthState(
            prefs.getLong(idKey, 0L),
            prefs.getString(tokenKey, null)
        )
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun postApiService(): PostApiService
    }

    private val entryPoint =
        EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)


    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authState.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(idKey, id)
            putString(tokenKey, token)
            commit()
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _authState.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
        sendPushToken()
    }

    @Synchronized
    fun sendPushToken(token: String? = null) {
        val request = OneTimeWorkRequestBuilder<SendPushTokenWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                Data.Builder()
                    .putString(SendPushTokenWorker.TOKEN_KEY, token)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                SendPushTokenWorker.NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )

//        CoroutineScope(Dispatchers.Default).launch {
//            try {
//                val pushToken = PushToken(token ?: FirebaseMessaging.getInstance().token.await())
//
//                PostsApi.retrofitService.sendPushToken(pushToken)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }

    suspend fun login(login: String, pass: String, context: Context) {
        try {
            val response = entryPoint.postApiService().login(login, pass)
            if (!response.isSuccessful) {
                when (response.code()) {
                    400, 404 -> Toast.makeText(
                        context,
                        "Неверный логин или пароль",
                        Toast.LENGTH_LONG
                    ).show()

                    else -> throw ApiError(response.code(), response.message())
                }
                return
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            setAuth(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    suspend fun registration(name: String, login: String, pass: String, context: Context) {
        try {
            val response = entryPoint.postApiService().registration(name, login, pass)
            if (!response.isSuccessful) {
                when (response.code()) {
                    400, 404, 403 -> Toast.makeText(
                        context,
                        "Такой пользователь уже существует",
                        Toast.LENGTH_LONG
                    ).show()

                    else -> throw ApiError(response.code(), response.message())
                }
                return
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            setAuth(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}

data class AuthState(
    val id: Long? = 0L,
    val token: String? = null
)