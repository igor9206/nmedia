package ru.netology.nmedia.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dto.PushToken
import javax.inject.Inject

class SendPushTokenWorker @Inject constructor(
    context: Context,
    parameters: WorkerParameters,
    private val postApiService: PostApiService
) : CoroutineWorker(context, parameters) {

    companion object {
        const val NAME = "SendPushTokenWorker"
        const val TOKEN_KEY = "TOKEN_KEY"
    }

    override suspend fun doWork(): Result =
        try {
            val token = inputData.getString(TOKEN_KEY)
            val pushToken = PushToken(token ?: FirebaseMessaging.getInstance().token.await())

            postApiService.sendPushToken(pushToken)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }

}