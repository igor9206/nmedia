package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val postType = object : TypeToken<List<Post>>() {}.type

    private companion object {
        const val BASE_URL = "http://192.168.1.54:9999/api/"
        val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request = Request.Builder()
            .url("${BASE_URL}posts")
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        val responseString = response.body?.string() ?: error("Body is null")
        return gson.fromJson(responseString, postType)
    }

    override fun likeById(id: Long): Post {
        val requestLikeByMe = Request.Builder()
            .url("${BASE_URL}posts/${id}")
            .build()
        val responseLikeByMe =
            client.newCall(requestLikeByMe).execute().body?.string() ?: error("Body is null")
        val likeByMe: Boolean = gson.fromJson(responseLikeByMe, Post::class.java).likedByMe

        val request = if (likeByMe) {
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

        val call = client.newCall(request)
        val response = call.execute()
        val responseString = response.body?.string() ?: error("Body is null")
        return gson.fromJson(responseString, Post::class.java)
    }

    override fun share(id: Long) {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long) {
        TODO("Not yet implemented")
    }

    override fun save(post: Post): Post {
        val request = Request.Builder()
            .url("${BASE_URL}posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        val responseString = response.body?.string() ?: error("Body is null")
        return gson.fromJson(responseString, Post::class.java)
    }
}