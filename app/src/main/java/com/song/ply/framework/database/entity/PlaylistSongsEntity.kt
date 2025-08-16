package com.song.ply.framework.database.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "songs",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["playlistID", "path"], unique = true)
    ])
data class PlaylistSongsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playlistID: Int,
    var path: String?,
    var name: String?,
    var album: String?,
    var artist: String?,
    val duration: String?,
    val img: Uri?
)
