package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post

class PostRepositoryImpl() : PostRepository {

    override fun getAllAsync(callback: PostRepository.GetAllCallBack<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body() ?: throw RuntimeException("empty bode"))
                } else {
                    callback.onError(RuntimeException("error code: ${response.code()} with ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    override fun likeByIdAsync(
        callback: PostRepository.GetAllCallBack<Post>,
        likedByMe: Boolean,
        id: Long
    ) {
        val request = if (likedByMe) {
            PostsApi.retrofitService.unLikeByIdAsync(id)
        } else {
            PostsApi.retrofitService.likeByIdAsync(id)
        }

        request.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body() ?: throw RuntimeException("empty bode"))
                } else {
                    callback.onError(RuntimeException("error code: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    override fun share(id: Long) {
        TODO("Not yet implemented")
    }

    override fun saveAsync(callback: PostRepository.GetAllCallBack<Post>, post: Post) {
        PostsApi.retrofitService.saveAsync(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body() ?: throw RuntimeException("empty bode"))
                } else {
                    callback.onError(RuntimeException("error code: ${response.code()} with ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }

        })
    }

    override fun removeByIdAsync(callback: PostRepository.GetAllCallBack<Unit>, id: Long) {
        PostsApi.retrofitService.removeByIdAsync(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        callback.onSuccess(response.body() ?: throw RuntimeException("empty bode"))
                    } else {
                        callback.onError(RuntimeException("error code: ${response.code()} with ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }

}