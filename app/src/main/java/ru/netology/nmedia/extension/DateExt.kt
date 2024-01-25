package ru.netology.nmedia.extension

import ru.netology.nmedia.dto.Post
import java.time.OffsetDateTime

private val today = OffsetDateTime.now()
private val yesterday = today.minusDays(1)
private val weeksAgo = today.minusDays(2)

fun Post.twoWeeksAgo(): Boolean =
    weeksAgo.year == published.year && weeksAgo.dayOfYear == published.dayOfYear

fun Post.yesterday(): Boolean =
    yesterday.year == published.year && yesterday.dayOfYear == published.dayOfYear

fun Post.today(): Boolean =
    today.year == published.year && today.dayOfYear == published.dayOfYear

fun Post?.notToday(): Boolean = this == null || !today()

fun Post?.notYesterday(): Boolean = this == null || !yesterday()

fun Post?.notWeekAgo(): Boolean = this == null || !twoWeeksAgo()