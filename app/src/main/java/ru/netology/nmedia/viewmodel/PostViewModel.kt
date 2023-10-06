package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    likes = 0
//    share = 0,
//    video = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModelState())
    val data: LiveData<FeedModelState> = _data
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> = _postCreated

    init {
        load()
    }

    fun load() {
        thread {
            _data.postValue(FeedModelState(loading = true))

            try {
                val posts = repository.getAll()
                (FeedModelState(posts = posts, empty = posts.isEmpty()))
            } catch (e: Exception) {
                (FeedModelState(error = true))
            }
                .let(_data::postValue)
        }
    }

    fun likeById(id: Long) {
        thread {
            repository.likeById(id)
            load()
        }
    }

    fun share(id: Long) = repository.share(id)
    fun removeById(id: Long) = repository.removeById(id)

    fun changeContentAndSave(content: String) {
        thread {
            edited.value?.let {
                val text = content.trim()
                if (it.content != content) {
                    repository.save(it.copy(content = text))
                    load()
                }
                _postCreated.postValue(Unit)
                edited.postValue(empty)
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

}