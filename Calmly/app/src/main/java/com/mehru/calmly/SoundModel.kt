package com.mehru.calmly

data class SoundModel(
    val title: String,
    val soundResId: Int,
    val imageResId: Int,
    var isPlaying: Boolean = false
)
