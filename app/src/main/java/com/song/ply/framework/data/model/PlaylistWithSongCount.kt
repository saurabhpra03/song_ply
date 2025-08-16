package com.song.ply.framework.data.model

data class PlaylistWithSongCount(
    val id: Int,
    var name: String,
    val songCount: Int,
)