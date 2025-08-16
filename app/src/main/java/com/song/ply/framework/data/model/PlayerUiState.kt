package com.song.ply.framework.data.model

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L
)