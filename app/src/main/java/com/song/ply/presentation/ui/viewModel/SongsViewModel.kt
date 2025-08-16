package com.song.ply.presentation.ui.viewModel

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.song.ply.R
import com.song.ply.framework.data.model.MenuOptions
import com.song.ply.framework.data.model.Songs
import com.song.ply.framework.data.repository.songs.SongsRepository
import com.song.ply.presentation.utils.Const.toast
import com.song.ply.presentation.utils.Resource
import com.song.ply.presentation.utils.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val repository: SongsRepository,
    private val sharedPref: SharedPref
) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> get() = _isDarkTheme.asStateFlow()

    private val _songListFlow = MutableStateFlow<Resource<List<Songs>>?>(null)
    val songListFlow: StateFlow<Resource<List<Songs>>?> get() = _songListFlow

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> get() = _searchText

    fun onSearchTextChange(query: String) { _searchText.value = query }

    init {
        toggleTheme()
    }

    fun fetchSongs() = viewModelScope.launch {
        _songListFlow.value = Resource.Loading
        _songListFlow.value = repository.fetchAllAudioFiles()
    }

    fun toggleTheme() {
        val newTheme = !isDarkTheme.value
        _isDarkTheme.value = newTheme
        sharedPref.setTheme(newTheme)
    }

    fun fetchMenuOptions(): List<MenuOptions> = listOf(
        MenuOptions(
            id = 1,
            name = context.getString(R.string.add_to_playlist),
            icon = Icons.Rounded.AddCircleOutline
        ),
        MenuOptions(
            id = 2,
            name = context.getString(R.string.delete),
            icon = Icons.Rounded.DeleteOutline
        )
    )

    // Delete audio file
    fun deleteSong(
        context: Context,
        path: String,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        val uri = getAudioContentUriFromPath(context, path) ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For API 30+ (Android 11+): Needs user permission
            try {
                val pendingIntent = MediaStore.createDeleteRequest(
                    context.contentResolver,
                    listOf(uri)
                )
                val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                launcher.launch(intentSenderRequest)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // For API <30: Delete directly
            try {
                context.contentResolver.delete(uri, null, null)
                context.toast(context.getString(R.string.deleted_successfully))
                fetchSongs()
            } catch (e: SecurityException) {
                context.toast(context.getString(R.string.permission_denied))
            } catch (e: Exception) {
                context.toast(context.getString(R.string.something_went_wrong))
            }
        }
    }

    private fun getAudioContentUriFromPath(context: Context, path: String): Uri? {
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media._ID)
        val selection = "${MediaStore.Audio.Media.DATA} = ?"
        val selectionArgs = arrayOf(path)

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(idColumn)
                return ContentUris.withAppendedId(collection, id)
            }
        }

        return null
    }

}