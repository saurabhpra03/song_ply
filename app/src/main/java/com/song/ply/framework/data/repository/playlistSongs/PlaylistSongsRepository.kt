package com.song.ply.framework.data.repository.playlistSongs

import com.song.ply.framework.database.entity.PlaylistSongsEntity
import com.song.ply.presentation.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PlaylistSongsRepository {
    suspend fun add(playlistSongsEntity: PlaylistSongsEntity): Resource<String>
    fun getSongsFromPlaylistID(id: Int): Flow<List<PlaylistSongsEntity>>
    suspend fun deleteSong(id: Int): Resource<String>
}