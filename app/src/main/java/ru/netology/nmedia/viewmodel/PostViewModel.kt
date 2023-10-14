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

class PostViewModel(private val application: Application) : AndroidViewModel(application) {

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
        _data.postValue(FeedModelState(loading = true))

        repository.getAllAsync(object : PostRepository.GetAllCallBack<List<Post>> {
            override fun onSuccess(item: List<Post>) {
                _data.postValue(FeedModelState(posts = item, empty = item.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModelState(error = true))
            }
        })
    }


    fun likeById(id: Long, likedByMe: Boolean) {
        val posts = data.value?.posts
        repository.likeByIdAsync(object : PostRepository.GetAllCallBack<Post> {
            override fun onSuccess(item: Post) {
                if (posts != null) {
                    _data.postValue(
                        FeedModelState(
                            posts = posts.map {
                                if (it.id == item.id) {
                                    item
                                } else it
                            }
                        )
                    )
                }
            }

            override fun onError(e: Exception) {
                println(e)
            }
        }, likedByMe, id)
    }

    fun share(id: Long) = repository.share(id)
    fun removeById(id: Long) {
        val posts = data.value?.posts
        repository.removeByIdAsync(object : PostRepository.GetAllCallBack<Any> {
            override fun onSuccess(item: Any) {
                if (posts != null) {
                    _data.postValue(
                        FeedModelState(
                            posts = posts.filter { it.id != id }
                        )
                    )
                }
            }

            override fun onError(e: Exception) {
                println(e)
            }
        }, id)
    }

    fun changeContentAndSave(content: String) {
        edited.value?.let {
            val text = content.trim()
            if (it.content != content) {
                repository.saveAsync(object : PostRepository.GetAllCallBack<Post> {
                    override fun onSuccess(item: Post) {
                        load()
                        _postCreated.postValue(Unit)
                        edited.postValue(empty)
                    }

                    override fun onError(e: Exception) {
                        println(e)
                    }
                }, it.copy(content = text))
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

}