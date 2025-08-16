package com.song.ply.framework.data.repository.playlist

import com.song.ply.framework.data.model.PlaylistWithSongCount
import com.song.ply.framework.database.entity.PlaylistEntity
import com.song.ply.presentation.utils.Resource

interface PlaylistRepository {
    suspend fun add(playlistEntity: PlaylistEntity): Resource<String>
    suspend fun updateName(playlistEntity: PlaylistEntity): Resource<String>
    suspend fun getPlaylist(): List<PlaylistWithSongCount>
    suspend fun deletePlaylist(id: Int): Resource<String>
}