package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post


private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

interface PostApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @POST("posts/{id}/likes")
    suspend fun likeByIdAsync(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun unLikeByIdAsync(@Path("id") id: Long): Response<Post>

    suspend fun share(id: Long)

    @POST("posts")
    suspend fun saveAsync(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeByIdAsync(@Path("id") id: Long): Response<Unit>
}

val logger = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

val client = OkHttpClient.Builder().addInterceptor(logger).build()

val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object PostsApi {
    val retrofitService by lazy {
        retrofit.create(PostApiService::class.java)
    }
}