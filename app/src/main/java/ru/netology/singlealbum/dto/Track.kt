package ru.netology.singlealbum.dto

data class Track(
    val id: Int,
    val file: String,
    var duration: String? = "",
    var isPlaying: Boolean = false,
)