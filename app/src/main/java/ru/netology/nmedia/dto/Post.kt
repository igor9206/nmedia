package ru.netology.nmedia.dto

import kotlin.math.abs
import kotlin.math.floor

data class Post(
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    var likedByMe: Boolean,
    var likes: Int,
    var share: Int
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