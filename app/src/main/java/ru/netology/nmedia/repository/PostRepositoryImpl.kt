package ru.netology.nmedia.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.TerminalSeparatorType
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import ru.netology.nmedia.R
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.di.ResourceService
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.ItemSeparator
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.extension.notToday
import ru.netology.nmedia.extension.notWeekAgo
import ru.netology.nmedia.extension.notYesterday
import ru.netology.nmedia.extension.today
import ru.netology.nmedia.extension.twoWeeksAgo
import ru.netology.nmedia.extension.yesterday
import ru.netology.nmedia.model.PhotoModel
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random


@Singleton
class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val postApiService: PostApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb,
    resourceService: ResourceService,
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(
            pageSize = 5,
            enablePlaceholders = true,
            maxSize = 15
        ),
        pagingSourceFactory = { dao.getPagingSource() },
        remoteMediator = PostRemoteMediator(
            service = postApiService,
            postDao = dao,
            postRemoteKeyDao = postRemoteKeyDao,
            db = appDb
        )
    ).flow
        .map {
            it.map { postEntity ->
                postEntity.toDto()
            }
                .insertSeparators(TerminalSeparatorType.SOURCE_COMPLETE) { previous: Post?, next: Post? ->
                    when {
                        previous.notToday() && next?.today() == true -> {
                            ItemSeparator(
                                Random.nextLong(),
                                resourceService.getString(R.string.today)
                            )
                        }

                        previous.notYesterday() && next?.yesterday() == true -> {
                            ItemSeparator(
                                Random.nextLong(),
                                resourceService.getString(R.string.yesterday)
                            )
                        }

                        previous.notWeekAgo() && next?.twoWeeksAgo() == true -> {
                            ItemSeparator(
                                Random.nextLong(),
                                resourceService.getString(R.string.last_week)
                            )
                        }

                        previous?.id?.rem(5) == 0L -> {
                            Ad(Random.nextLong(), "figma.jpg")
                        }

                        else -> null
                    }
                }
        }


    override suspend fun getAll() {
        try {
            val response = postApiService.getAll()
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

    override fun getNewer(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = postApiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.map { it.copy(hidden = true) }.toEntity())
            emit(body.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
//        .flowOn(Dispatchers.Default)

    override suspend fun save(post: Post) {
        try {
            val response = postApiService.saveAsync(post)
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
            val response = postApiService.removeByIdAsync(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(post: Post) {
        try {
            if (post.likedByMe) {
                dao.unlikeById(post.id)
                val response = postApiService.unLikeByIdAsync(post.id)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                dao.insert(PostEntity.fromDto(body.copy(hidden = false)))
            } else {
                dao.likeById(post.id)
                val response = postApiService.likeByIdAsync(post.id)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                dao.insert(PostEntity.fromDto(body.copy(hidden = false)))
            }
        } catch (e: IOException) {
            dao.insert(PostEntity.fromDto(post))
            throw NetworkError
        } catch (e: Exception) {
            dao.insert(PostEntity.fromDto(post))
            throw UnknownError
        }
    }

    override suspend fun loadFromLocalDB() {
        dao.hidden()
    }

    override suspend fun saveWithAttachment(post: Post, photo: PhotoModel) {
        try {
            val saveMedia = saveMedia(photo.file)
            if (!saveMedia.isSuccessful) {
                throw ApiError(saveMedia.code(), saveMedia.message())
            }
            val media = saveMedia.body() ?: throw ApiError(saveMedia.code(), saveMedia.message())

            val response =
                postApiService.saveAsync(
                    post.copy(
                        attachment = Attachment(
                            media.id,
                            AttachmentType.IMAGE
                        )
                    )
                )
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

    private suspend fun saveMedia(file: File): Response<Media> {
        val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        return postApiService.saveMedia(part)
    }

}