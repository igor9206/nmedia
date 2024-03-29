package ru.netology.nmedia.dto

import java.time.OffsetDateTime


sealed interface FeedItem {
    val id: Long
}

data class Post(
    override val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: OffsetDateTime,
    var likedByMe: Boolean,
    var likes: Int,
    val attachment: Attachment? = null,
    var hidden: Boolean,
    val ownedByMe: Boolean = false
) : FeedItem {
    fun numericFormat(number: Int): String {
        return when {
            number in 1000..9_999 -> (number / 1000.0).toString().take(3) + "K"
            number in 10_000..999_999 -> (number / 1000).toString().take(3) + "K"
            number >= 1_000_000 -> (number / 1000000.0).toString().take(3) + "M"
            else -> {
                number.toString()
            }
        }
    }
}

data class ItemSeparator(
    override val id: Long,
    val text: String
) : FeedItem

data class Ad(
    override val id: Long,
    val image: String
) : FeedItem

data class Attachment(
    val url: String,
    val type: AttachmentType
)

enum class AttachmentType {
    IMAGE
}