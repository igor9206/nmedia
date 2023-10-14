package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val postType = object : TypeToken<List<Post>>() {}.type

    private companion object {
        const val BASE_URL = "http://192.168.1.54:9999/api/slow/"
        val jsonType = "application/json".toMediaType()
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallBack<List<Post>>) {
        val request = Request.Builder()
            .url("${BASE_URL}posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("body is null")
                        callback.onSuccess(gson.fromJson(body, postType))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun likeByIdAsync(
        callback: PostRepository.GetAllCallBack<Post>,
        likedByMe: Boolean,
        id: Long
    ) {
        val request = if (likedByMe) {
            Request.Builder()
                .url("${BASE_URL}posts/${id}/likes")
                .delete("".toRequestBody())
                .build()
        } else {
            Request.Builder()
                .url("${BASE_URL}posts/${id}/likes")
                .post("".toRequestBody())
                .build()
        }

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("body is null")
                        callback.onSuccess(gson.fromJson(body, Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun share(id: Long) {
        TODO("Not yet implemented")
    }

    override fun removeByIdAsync(callback: PostRepository.GetAllCallBack<Any>, id: Long) {
        val request = Request.Builder()
            .url("${BASE_URL}posts/${id}")
            .delete("".toRequestBody())
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseCode = response.code
                        callback.onSuccess(responseCode)
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun saveAsync(callback: PostRepository.GetAllCallBack<Post>, post: Post) {
        val request = Request.Builder()
            .url("${BASE_URL}posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("body is null")
                        callback.onSuccess(gson.fromJson(body, Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }
}