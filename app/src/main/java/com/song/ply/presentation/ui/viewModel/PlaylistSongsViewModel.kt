package com.song.ply.presentation.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.song.ply.framework.data.repository.playlistSongs.PlaylistSongsRepository
import com.song.ply.framework.database.entity.PlaylistSongsEntity
import com.song.ply.presentation.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistSongsViewModel @Inject constructor(
    private val repository: PlaylistSongsRepository
) : ViewModel() {

    private val _addSongFlow = MutableStateFlow<Resource<String>?>(null)
    val addSongFlow: MutableStateFlow<Resource<String>?> get() = _addSongFlow

    private val _deleteSongFlow = MutableStateFlow<Resource<String>?>(null)
    val deleteSongFlow: MutableStateFlow<Resource<String>?> get() = _deleteSongFlow

    private val _songList = MutableStateFlow<List<PlaylistSongsEntity>>(emptyList())
    val songList: StateFlow<List<PlaylistSongsEntity>> get() = _songList

    fun addSongToPlaylist(playlistSongEntity: PlaylistSongsEntity) = viewModelScope.launch {
        _addSongFlow.value = Resource.Loading
        _addSongFlow.value = repository.add(playlistSongEntity)
    }

    fun fetchSongsFromPlaylist(id: Int) {
        viewModelScope.launch {
            repository.getSongsFromPlaylistID(id)
                .collect { songs ->
                    _songList.value = songs
                }
        }
    }

    fun deleteSong(id: Int) = viewModelScope.launch {
        _deleteSongFlow.value = Resource.Loading
        _deleteSongFlow.value = repository.deleteSong(id)
    }

    fun clearFlow(){
        _addSongFlow.value = null
        _deleteSongFlow.value = null
    }

}