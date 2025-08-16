package com.song.ply.presentation.ui.view.songs

import android.net.Uri
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.song.ply.framework.data.model.Songs
import com.song.ply.presentation.ui.view.common.AppLoader
import com.song.ply.presentation.ui.view.common.ItemSongsList
import com.song.ply.presentation.ui.view.common.TextEmpty
import com.song.ply.presentation.ui.viewModel.PlayerManagerViewModel
import com.song.ply.presentation.ui.viewModel.SongsViewModel
import com.song.ply.presentation.utils.Const.getAudioThumbnail
import com.song.ply.presentation.utils.Const.logD
import com.song.ply.presentation.utils.Resource

@Composable
fun SongsScreen(
    listModifier: Modifier,
    emptyTextModifier: Modifier,
    showMiniPlayer: MutableState<Boolean>,
    selectedSongPath: MutableState<String>,
    onMoreClick: (Songs) -> Unit,
    playerManagerViewModel: PlayerManagerViewModel,
    songsViewModel: SongsViewModel
) {

    val TAG = "SongsScreen"

    val context = LocalContext.current

    val state = rememberLazyGridState()

    var list = remember { mutableStateListOf<Songs>() }
    val songListFlow = songsViewModel.songListFlow.collectAsState()
    songListFlow.value?.let {
        when (it) {
            is Resource.Loading -> AppLoader()

            is Resource.Success -> {
                list = it.data.toMutableStateList()

            }

            is Resource.Failed -> {
                TAG.logD("===========Failed============")
            }
        }
    }

    val searchQuery by songsViewModel.searchText.collectAsState()
    val filteredItems = remember(searchQuery, list) {
        if (searchQuery.trim().isEmpty()) {
            list
        } else {
            list.filter {
                it.name?.contains(searchQuery.trim(), ignoreCase = true) == true ||
                        it.artist?.contains(searchQuery.trim(), ignoreCase = true) == true
            }
        }
    }

    selectedSongPath.value = playerManagerViewModel.currentSong.collectAsState().value?.path ?: ""

    when {
        list.isNotEmpty() -> LazyVerticalGrid(
            modifier = listModifier,
            state = state,
            reverseLayout = false,
            columns = GridCells.Fixed(1)
        ) {
            items(count = filteredItems.size) { index ->
                val audioData = filteredItems[index]
                val audioBitmap = context.getAudioThumbnail(Uri.parse(audioData.path))

                ItemSongsList(
                    index = index,
                    selectedSongPath = selectedSongPath,
                    audioBitmap = audioBitmap,
                    name = audioData.name ?: "",
                    artist = audioData.artist ?: "",
                    path = audioData.path ?: "",
                    onClick = {
                        showMiniPlayer.value = true
                        playerManagerViewModel.playAudio(audioData, filteredItems)
                    },
                    onMoreClick = {
                        if (selectedSongPath.value != (audioData.path ?: "")){
                            onMoreClick(audioData)
                        }
                    }
                )
            }
        }

        else -> TextEmpty(emptyTextModifier)
    }
}