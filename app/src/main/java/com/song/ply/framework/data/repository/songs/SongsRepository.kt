package com.song.ply.framework.data.repository.songs

import com.song.ply.framework.data.model.Songs
import com.song.ply.presentation.utils.Resource

interface SongsRepository {
    suspend fun fetchAllAudioFiles(): Resource<List<Songs>>
}