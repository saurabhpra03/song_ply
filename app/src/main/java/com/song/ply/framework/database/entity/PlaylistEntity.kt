package com.song.ply.framework.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String
)