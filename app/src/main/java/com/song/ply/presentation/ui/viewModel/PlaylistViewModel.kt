package com.song.ply.presentation.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.song.ply.framework.data.model.PlaylistWithSongCount
import com.song.ply.framework.data.repository.playlist.PlaylistRepository
import com.song.ply.framework.database.entity.PlaylistEntity
import com.song.ply.presentation.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val repository: PlaylistRepository
) : ViewModel() {

    private val _createFlow = MutableStateFlow<Resource<String>?>(null)
    val createFlow: MutableStateFlow<Resource<String>?> get() = _createFlow

    private val _updateNameFlow = MutableStateFlow<Resource<String>?>(null)
    val updateNameFlow: MutableStateFlow<Resource<String>?> get() = _updateNameFlow

    private val _deletePlayList = MutableStateFlow<Resource<String>?>(null)
    val deletePlayList: MutableStateFlow<Resource<String>?> get() = _deletePlayList

    private val _playlistFlow = MutableStateFlow<List<PlaylistWithSongCount>>(emptyList())
    val playlistFlow: MutableStateFlow<List<PlaylistWithSongCount>> get() = _playlistFlow

    fun fetchPlayList() = viewModelScope.launch{
        _playlistFlow.value = repository.getPlaylist()
    }

    fun createPlayList(playlistEntity: PlaylistEntity) = viewModelScope.launch {
        _createFlow.value = Resource.Loading
        _createFlow.value = repository.add(playlistEntity)
    }

    fun updatePlayListName(playlistEntity: PlaylistEntity) = viewModelScope.launch {
        _updateNameFlow.value = Resource.Loading
        _updateNameFlow.value = repository.updateName(playlistEntity)
    }

    fun deletePlayList(id: Int) = viewModelScope.launch {
        _deletePlayList.value = Resource.Loading
        _deletePlayList.value = repository.deletePlaylist(id)
    }

    fun clearFlow(){
        _createFlow.value = null
        _updateNameFlow.value = null
        _deletePlayList.value = null
    }
}