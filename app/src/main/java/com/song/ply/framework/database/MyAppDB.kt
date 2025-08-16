package com.song.ply.framework.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.song.ply.framework.database.dao.PlaylistDao
import com.song.ply.framework.database.dao.PlaylistSongsDao
import com.song.ply.framework.database.entity.PlaylistEntity
import com.song.ply.framework.database.entity.PlaylistSongsEntity

@Database(
    entities = [PlaylistEntity::class, PlaylistSongsEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MyAppDB : RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao
    abstract fun songsDao(): PlaylistSongsDao
}