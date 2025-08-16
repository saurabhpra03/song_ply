package com.song.ply.framework.data.repository.playlistSongs

import android.content.Context
import com.song.ply.R
import com.song.ply.framework.database.dao.PlaylistSongsDao
import com.song.ply.framework.database.entity.PlaylistSongsEntity
import com.song.ply.presentation.utils.Const.logD
import com.song.ply.presentation.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaylistSongsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dao: PlaylistSongsDao
) : PlaylistSongsRepository {

    private val TAG = PlaylistSongsRepositoryImpl::class.java.simpleName

    override suspend fun add(playlistSongsEntity: PlaylistSongsEntity) =
        withContext(Dispatchers.IO) {
            try {
                val existsCount = dao.isSongInPlaylist(
                    playlistSongsEntity.playlistID,
                    playlistSongsEntity.path?.trim() ?: ""
                )
                if (existsCount > 0) {
                    return@withContext Resource.Failed(context.getString(R.string.song_already_exists_in_playlist))
                }

                val response = dao.addSong(playlistSongsEntity)
                if (response > 0) {
                    Resource.Success(context.getString(R.string.song_added_successfully))
                } else {
                    Resource.Failed(context.getString(R.string.something_went_wrong))
                }
            } catch (e: Exception) {
                TAG.logD("add, exception: ${e.message}")
                Resource.Failed(context.getString(R.string.something_went_wrong))
            }
        }

    override fun getSongsFromPlaylistID(id: Int) = dao.getSongsFromPlaylistID(id)

    override suspend fun deleteSong(id: Int) = withContext(Dispatchers.IO) {
        try {
            val response = dao.deleteSong(id)
            when {
                response > 0 -> Resource.Success(context.getString(R.string.song_deleted_successfully))
                else -> Resource.Failed(context.getString(R.string.something_went_wrong))
            }
        } catch (e: Exception) {
            TAG.logD("deleteSong, exception: ${e.message}")
            Resource.Failed(context.getString(R.string.something_went_wrong))
        }
    }
}