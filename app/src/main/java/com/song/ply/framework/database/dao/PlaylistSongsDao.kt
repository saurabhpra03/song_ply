package com.song.ply.framework.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.song.ply.framework.database.entity.PlaylistSongsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistSongsDao {

    @Insert
    suspend fun addSong(songs: PlaylistSongsEntity): Long

    @Query("SELECT COUNT(*) FROM songs WHERE playlistID = :playlistId AND path = :path")
    suspend fun isSongInPlaylist(playlistId: Int, path: String?): Int

    @Query("SELECT * FROM songs WHERE playlistID = :playlistID ORDER BY name ASC")
    fun getSongsFromPlaylistID(playlistID: Int): Flow<List<PlaylistSongsEntity>>

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteSong(id: Int): Int
}