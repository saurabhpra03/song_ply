package com.song.ply.framework.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.song.ply.framework.data.model.PlaylistWithSongCount
import com.song.ply.framework.database.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert
    suspend fun createPlayList(playListEntity: PlaylistEntity): Long

    @Query("UPDATE playlist SET name = :name WHERE id = :id")
    suspend fun updatePlaylistName(id: Int, name: String): Int

    @Query(
        """
    SELECT p.id, p.name, COUNT(s.id) AS songCount
    FROM playlist p
    LEFT JOIN songs s ON p.id = s.playlistID
    GROUP BY p.id
    ORDER BY p.name ASC
"""
    )
    suspend fun getPlayList(): List<PlaylistWithSongCount>?

    @Query("SELECT id FROM songs")
    fun songChanges(): Flow<List<Long>>

    @Query("DELETE FROM playlist WHERE id = :id")
    suspend fun delete(id: Int): Int

}