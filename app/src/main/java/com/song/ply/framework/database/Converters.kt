package com.song.ply.framework.database

import android.net.Uri
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromUri(uri: Uri?): String?{
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(data: String?): Uri?{
        return data?.let { Uri.parse(data) }
    }
}