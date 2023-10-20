package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun getAllAsync(callback: GetAllCallBack<List<Post>>)
    fun likeByIdAsync(callback: GetAllCallBack<Post>, likedByMe: Boolean, id: Long)
    fun share(id: Long)
    fun saveAsync(callback: GetAllCallBack<Post>, post: Post)
    fun removeByIdAsync(callback: GetAllCallBack<Unit>, id: Long)

    interface GetAllCallBack<T> {
        fun onSuccess(item: T)
        fun onError(e: Exception)
    }
}