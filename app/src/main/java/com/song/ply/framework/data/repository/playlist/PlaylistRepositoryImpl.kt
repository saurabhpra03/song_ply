package com.song.ply.framework.data.repository.playlist

import android.content.Context
import com.song.ply.R
import com.song.ply.framework.database.dao.PlaylistDao
import com.song.ply.framework.database.entity.PlaylistEntity
import com.song.ply.presentation.utils.Const.logD
import com.song.ply.presentation.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dao: PlaylistDao
): PlaylistRepository {

    private val TAG  = PlaylistRepositoryImpl::class.java.simpleName

    override suspend fun add(playlistEntity: PlaylistEntity) = withContext(Dispatchers.IO) {
        try {
            val response = dao.createPlayList(playlistEntity)
            when {
                response > 0 -> Resource.Success(context.getString(R.string.playlist_created_successfully))
                else -> Resource.Failed(context.getString(R.string.something_went_wrong))
            }
        } catch (e: Exception) {
            TAG.logD("add, exception: ${e.message}")
            Resource.Failed(context.getString(R.string.something_went_wrong))
        }
    }

    override suspend fun updateName(playlistEntity: PlaylistEntity) = withContext(Dispatchers.IO) {
        try {
            val response = dao.updatePlaylistName(playlistEntity.id, playlistEntity.name)

            when {
                response > 0 -> Resource.Success(context.getString(R.string.playlist_updated_successfully))
                else -> Resource.Failed(context.getString(R.string.something_went_wrong))
            }

        } catch (e: Exception) {
            TAG.logD("updateName, exception: ${e.message}")
            Resource.Failed(context.getString(R.string.something_went_wrong))
        }
    }

    override suspend fun getPlaylist() = withContext(Dispatchers.IO){
        try {
            dao.getPlayList() ?: run { emptyList() }
        }catch (e: Exception){
            TAG.logD("getPlayLists, exception: ${e.message}")
            emptyList()
        }
    }

    override suspend fun deletePlaylist(id: Int) = withContext(Dispatchers.IO) {
        try {
            val response = dao.delete(id)

            when {
                response > 0 -> Resource.Success(context.getString(R.string.deleted_successfully))
                else -> Resource.Failed(context.getString(R.string.playlist_deleted_successfully))
            }
        } catch (e: Exception) {
            TAG.logD("deletePlayList, exception: ${e.message}")
            Resource.Failed(context.getString(R.string.no_data_found))
        }
    }
}