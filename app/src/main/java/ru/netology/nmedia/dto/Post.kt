package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    var likedByMe: Boolean,
    var likes: Int,
    var share: Int,
    var view: Int = 0,
    val video: String = ""
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