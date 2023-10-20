package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean,
    var likes: Int,
    val attachment: Attachment?
) {
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

data class Attachment(
    val url: String,
    val description: String,
    val type: String
)