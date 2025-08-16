package com.song.ply.framework.data.repository.songs

import com.song.ply.R
import android.content.Context
import android.provider.MediaStore
import androidx.core.net.toUri
import com.song.ply.framework.data.model.Songs
import com.song.ply.presentation.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SongsRepository {

    override suspend fun fetchAllAudioFiles(): Resource<List<Songs>> = withContext(Dispatchers.IO){
        try {

            val list: MutableList<Songs> = mutableListOf()
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
            )

            val cursor = context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
            )

            cursor?.let { data ->
                while (data.moveToNext()) {
                    val path = data.getString(0)
                    val name = data.getString(1)
                    val album = data.getString(2)
                    val artist = data.getString(3)
                    val duration = data.getString(4)
                    val albumId = data.getString(5)

                    // Query album art from album table
                    var albumArt: String? = null
                    val albumCursor = context.contentResolver.query(
                        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.Audio.Albums.ALBUM_ART),
                        "${MediaStore.Audio.Albums._ID} = ?",
                        arrayOf(albumId.toString()),
                        null
                    )

                    albumCursor?.use {
                        if (it.moveToFirst()) {
                            albumArt = it.getString(0) // Path to image file
                        }
                    }

                    val imageUri = albumArt?.let { "file://$it".toUri() }


                    list.add(Songs(path = path, name = name, album = album, artist = artist, duration = duration, img = imageUri))
                }
                cursor.close()
            }

            if (list.isNotEmpty())
                Resource.Success(list.sortedBy { it.name?.lowercase()?.trim() })
            else
                Resource.Failed(context.getString(R.string.no_data_found))

        } catch (e: Exception) {
            Resource.Failed(context.getString(R.string.no_data_found))
        }

    }
}