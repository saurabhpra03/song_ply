package com.song.ply.presentation.ui.view.playlistSongs

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.song.ply.R
import com.song.ply.framework.data.model.Songs
import com.song.ply.framework.database.entity.PlaylistEntity
import com.song.ply.framework.database.entity.PlaylistSongsEntity
import com.song.ply.presentation.ui.theme.Dimen
import com.song.ply.presentation.ui.view.common.BottomSheetMenuOptions
import com.song.ply.presentation.ui.view.common.DialogDeleteAlert
import com.song.ply.presentation.ui.view.common.HeaderIcon
import com.song.ply.presentation.ui.view.common.ItemSongsList
import com.song.ply.presentation.ui.view.common.SearchField
import com.song.ply.presentation.ui.view.common.TextEmpty
import com.song.ply.presentation.ui.viewModel.PlayerManagerViewModel
import com.song.ply.presentation.ui.viewModel.PlaylistSongsViewModel
import com.song.ply.presentation.utils.Const.getAudioThumbnail
import com.song.ply.presentation.utils.Const.toast
import com.song.ply.presentation.utils.Resource

@Composable
fun PlaylistSongsScreen(
    navController: NavController,
    playListEntity: PlaylistEntity,
    showMiniPlayer: MutableState<Boolean>,
    selectedSongPath: MutableState<String>,
    playerManagerViewModel: PlayerManagerViewModel,
    playlistSongsViewModel: PlaylistSongsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val isSearch = remember { mutableStateOf(false) }
    // Search bar text input
    val searchText = remember { mutableStateOf("") }

    val list = playlistSongsViewModel.songList.collectAsState().value
    playlistSongsViewModel.fetchSongsFromPlaylist(playListEntity.id)
    val filteredItems = remember(searchText.value, list) {
        if (searchText.value.isEmpty()) {
            list
        } else {
            list.filter {
                it.name?.contains(searchText.value, ignoreCase = true) == true ||
                        it.artist?.contains(searchText.value, ignoreCase = true) == true
            }
        }
    }

    val state = rememberLazyGridState()

    // State for showing delete confirmation dialog
    val showDeleteAlert = remember { mutableStateOf(false) }
    // Holds the playlist data to be deleted
    val deletePlaylistSongEntity = remember { mutableStateOf<PlaylistSongsEntity?>(null) }
    playlistSongsViewModel.deleteSongFlow.collectAsState().value?.let {
        when(it){
            is Resource.Loading -> {}

            is Resource.Success -> {
                playlistSongsViewModel.fetchSongsFromPlaylist(playListEntity.id)
                deletePlaylistSongEntity.value = null
                playlistSongsViewModel.clearFlow()
            }

            is Resource.Failed -> {
                context.toast(it.message)
                deletePlaylistSongEntity.value = null
                playlistSongsViewModel.clearFlow()
            }
        }
    }
    DialogDeleteAlert(
        showDeleteFileAlert = showDeleteAlert,
        title = stringResource(R.string.delete_playlist),
        fileName = deletePlaylistSongEntity.value?.name ?: "Unknown",
        onClickDelete = {
            playlistSongsViewModel.deleteSong(deletePlaylistSongEntity.value?.id ?: 0)
            showDeleteAlert.value = false
        }
    )


    val showBottomSheet = remember { mutableStateOf(false) }
    val playlistSongEntity = remember { mutableStateOf<PlaylistSongsEntity?>(null) }
   BottomSheetMenuOptions(
       showBottomSheet = showBottomSheet,
       title = playlistSongEntity.value?.name ?: "",
       isDeleteOnly = true,
       onEdit = {},
       onDelete = {
           deletePlaylistSongEntity.value = playlistSongEntity.value
           playlistSongEntity.value = null
           showBottomSheet.value = false
           showDeleteAlert.value = true
       },
       onDismiss = {
           playlistSongEntity.value = null
           showBottomSheet.value = false
       }
   )


    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (
            refBack,
            refTitle,
            refSearch,
            refSearchIcon,
            refSongs,
            refEmpty) = createRefs()

        HeaderIcon(
            modifier = Modifier
                .constrainAs(refBack) {
                    start.linkTo(parent.start, Dimen.dimen20)
                    top.linkTo(parent.top, Dimen.dimen20)
                },
            icon = Icons.Rounded.ArrowBackIosNew,
            onClick = {
                navController.popBackStack()
            }
        )

        AnimatedVisibility(
            modifier = Modifier
                .constrainAs(refTitle) {
                    start.linkTo(refBack.end, Dimen.dimen10)
                    top.linkTo(refBack.top)
                    end.linkTo(refSearchIcon.start, Dimen.dimen10)
                    bottom.linkTo(refBack.bottom)
                    width = Dimension.fillToConstraints
                },
            visible = !isSearch.value,
            content = {
                Text(
                    text = playListEntity.name,
                    style = MaterialTheme.typography.headlineMedium
                        .copy(color = MaterialTheme.colorScheme.primary),
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )

        AnimatedVisibility(
            modifier = Modifier
                .constrainAs(refSearch) {
                    start.linkTo(refBack.end, Dimen.dimen30)
                    top.linkTo(parent.top, Dimen.dimen20)
                    end.linkTo(refSearchIcon.start, Dimen.dimen30)
                    width = Dimension.fillToConstraints
                },
            visible = isSearch.value,
            content = {
                SearchField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    searchText = searchText,
                    onValueChange = {
                        searchText.value = it
                    }
                )
            }
        )

        HeaderIcon(
            modifier = Modifier
                .constrainAs(refSearchIcon) {
                    top.linkTo(parent.top, Dimen.dimen20)
                    end.linkTo(parent.end, Dimen.dimen20)
                },
            icon = if (isSearch.value) Icons.Rounded.Close else Icons.Rounded.Search,
            onClick = {
                isSearch.value = !isSearch.value
            }
        )

        val currentAudio = playerManagerViewModel.currentSong.collectAsState().value
        selectedSongPath.value = currentAudio?.path ?: ""

        when {
            list.isNotEmpty() -> LazyVerticalGrid(
                modifier = Modifier
                    .constrainAs(refSongs) {
                        start.linkTo(parent.start, Dimen.screenPadding)
                        top.linkTo(refBack.bottom, Dimen.dimen30)
                        end.linkTo(parent.end, Dimen.screenPadding)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                state = state,
                reverseLayout = false,
                columns = GridCells.Fixed(1)
            ) {
                items(count = filteredItems.size) { index ->
                    val songsData = filteredItems[index]
                    val audioBitmap = context.getAudioThumbnail(Uri.parse(songsData.path))

                    ItemSongsList(
                        index = index,
                        selectedSongPath = selectedSongPath,
                        audioBitmap = audioBitmap,
                        name = songsData.name ?: "",
                        artist = songsData.artist ?: "",
                        path = songsData.path ?: "",
                        onClick = {
                            showMiniPlayer.value = true
                            val audioDataList: List<Songs> =
                                filteredItems.map { it.toAudioData() }
                            playerManagerViewModel.playAudio(songsData.toAudioData(), audioDataList)
                        },
                        onMoreClick = {
                               playlistSongEntity.value = songsData
                               showBottomSheet.value = true
                        }
                    )
                }
            }

            else -> TextEmpty(
                Modifier
                    .constrainAs(refEmpty) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    })
        }
    }
}

fun PlaylistSongsEntity.toAudioData(): Songs {
    return Songs(
        path = path,
        name = name,
        album = album,
        artist = artist,
        duration = duration,
        img = img
    )
}