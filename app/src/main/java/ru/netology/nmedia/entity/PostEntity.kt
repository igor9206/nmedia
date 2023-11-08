package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post

@Entity
@TypeConverters(AttachmentConverter::class)
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean,
    var likes: Int,
    val attachment: Attachment?
) {
    fun toDto() = Post(id, author, authorAvatar, content, published, likedByMe, likes, attachment)

    companion object {
        fun fromDto(post: Post) = PostEntity(
            post.id,
            post.author,
            post.authorAvatar,
            post.content,
            post.published,
            post.likedByMe,
            post.likes,
            post.attachment
        )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)

class AttachmentConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromAttachment(attachment: Attachment?): String? {
        return if (attachment != null) gson.toJson(attachment) else null
    }

    @TypeConverter
    fun toAttachment(attachment: String?): Attachment? {
        return if (attachment != null) gson.fromJson(attachment, Attachment::class.java) else null
    }

}
