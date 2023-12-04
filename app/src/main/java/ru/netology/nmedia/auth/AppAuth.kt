package ru.netology.nmedia.auth

import android.content.Context
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.worker.SendPushTokenWorker
import java.io.IOException
import java.lang.IllegalStateException
import kotlin.coroutines.coroutineContext

class AppAuth private constructor(private val context: Context) {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow<AuthState>(
        AuthState(
            prefs.getLong(KEY_ID, 0L),
            prefs.getString(KEY_TOKEN, null)
        )
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

//    init {
//        sendPushToken()
//    }

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authState.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(KEY_ID, id)
            putString(KEY_TOKEN, token)
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

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_TOKEN = "token"

        @Volatile
        private var instance: AppAuth? = null

        fun getInstance() = synchronized(this) {
            instance
                ?: throw IllegalStateException("getInstance should be called only after initAuth")
        }

        fun initAuth(context: Context) = instance ?: synchronized(this) {
            instance ?: AppAuth(context).also { instance = it }
        }

        suspend fun login(login: String, pass: String, context: Context) {
            try {
                val response = PostsApi.retrofitService.login(login, pass)
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
                getInstance().setAuth(body.id, body.token)
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                throw UnknownError
            }
        }

        suspend fun registration(name: String, login: String, pass: String, context: Context) {
            try {
                val response = PostsApi.retrofitService.registration(name, login, pass)
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
                getInstance().setAuth(body.id, body.token)
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                throw UnknownError
            }
        }

    }
}

data class AuthState(
    val id: Long? = 0L,
    val token: String? = null
)