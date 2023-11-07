package ru.netology.nmedia.repository

import androidx.lifecycle.map
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {

    override val data = dao.getAll().map(List<PostEntity>::toDto)

    override suspend fun getAll() {
        try {
            val response = PostsApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.retrofitService.saveAsync(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
        try {
            val response = PostsApi.retrofitService.removeByIdAsync(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        val post = data.value?.find { it.id == id }
        post ?: return
        localLikeById(post)
        try {
            if (post.likedByMe) {
                val response = PostsApi.retrofitService.unLikeByIdAsync(post.id)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
            } else {
                val response = PostsApi.retrofitService.likeByIdAsync(post.id)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun localLikeById(post: Post) {
        if (post.likedByMe) {
            dao.insert(
                PostEntity.fromDto(
                    post.copy(
                        likes = post.likes - 1,
                        likedByMe = !post.likedByMe
                    )
                )
            )
        } else {
            dao.insert(
                PostEntity.fromDto(
                    post.copy(
                        likes = post.likes + 1,
                        likedByMe = !post.likedByMe
                    )
                )
            )
        }
    }

}