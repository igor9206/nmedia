package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    var likedByMe: Boolean,
    var likes: Int,
    var share: Int,
    var view: Int,
    val video: String
) {
    fun toDto() = Post(id, author, published, content, likedByMe, likes, share, view, video)

    companion object {
        fun fromDto(post: Post) = PostEntity(
            post.id,
            post.author,
            post.published,
            post.content,
            post.likedByMe,
            post.likes,
            post.share,
            post.view,
            post.video
        )
    }
}
