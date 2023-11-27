package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {

    val data: Flow<List<Post>>
    suspend fun getAll()
    fun getNewer(id: Long): Flow<Int>
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun loadFromLocalDB()
    suspend fun saveWithAttachment(post: Post, photo: PhotoModel)
}