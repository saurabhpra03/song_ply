package com.song.ply.presentation.ui.view.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.song.ply.R
import com.song.ply.framework.data.model.PlaylistWithSongCount
import com.song.ply.framework.database.entity.PlaylistEntity
import com.song.ply.presentation.ui.theme.Dimen
import com.song.ply.presentation.ui.view.common.AppLoader
import com.song.ply.presentation.ui.view.common.BottomSheetMenuOptions
import com.song.ply.presentation.ui.view.common.DialogDeleteAlert
import com.song.ply.presentation.ui.view.common.TextEmpty
import com.song.ply.presentation.ui.viewModel.PlaylistViewModel
import com.song.ply.presentation.ui.viewModel.SongsViewModel
import com.song.ply.presentation.utils.Const.toast
import com.song.ply.presentation.utils.Resource
import com.song.ply.presentation.utils.Screens

@Composable
fun PlaylistScreen(
    listModifier: Modifier,
    emptyTextModifier: Modifier,
    navController: NavController,
    songsViewModel: SongsViewModel,
    playlistViewModel: PlaylistViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    playlistViewModel.fetchPlayList()
    val lists = playlistViewModel.playlistFlow.collectAsState().value

    val searchQuery by songsViewModel.searchText.collectAsState()
    val filteredItems = remember(searchQuery.trim(), lists) {
        if (searchQuery.trim().isEmpty()) lists else lists.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
    }

    val state = rememberLazyGridState()

    // State for showing add/update playlist bottom sheet
    val showEditPlaylistBottomSheet = remember { mutableStateOf(false) }
    // Holds the playlist data to be add/update
    val playlistEntity = remember { mutableStateOf<PlaylistEntity?>(null) }
    BottomSheetManagePlayList(
        showBottomSheet = showEditPlaylistBottomSheet,
        playListEntity = playlistEntity,
        onUpdate = { playlistViewModel.fetchPlayList() }
    )

    // State for showing delete confirmation dialog
    val showDeleteAlert = remember { mutableStateOf(false) }
    // Holds the playlist data to be deleted
    val deletePlayListWithCount = remember { mutableStateOf<PlaylistWithSongCount?>(null) }


    // Confirmation dialog for deleting a playlist
    playlistViewModel.deletePlayList.collectAsState().value?.let {
        when(it){
            is Resource.Loading -> AppLoader()

            is Resource.Success -> {
                playlistViewModel.fetchPlayList()
                deletePlayListWithCount.value = null
                playlistViewModel.clearFlow()
            }

            is Resource.Failed -> {
                context.toast(it.message)
                deletePlayListWithCount.value = null
                playlistViewModel.clearFlow()
            }
        }
    }
    DialogDeleteAlert(
        showDeleteFileAlert = showDeleteAlert,
        title = stringResource(R.string.delete_playlist),
        fileName = deletePlayListWithCount.value?.name ?: "Unknown",
        onClickDelete = {
            playlistViewModel.deletePlayList(deletePlayListWithCount.value?.id ?: 0)
            showDeleteAlert.value = false
        }
    )

    val showBottomSheet = remember { mutableStateOf(false) }
    val playlistWithSongCount = remember { mutableStateOf<PlaylistWithSongCount?>(null) }
    BottomSheetMenuOptions(
        showBottomSheet = showBottomSheet,
        title = playlistWithSongCount.value?.name ?: "",
        onEdit = {
            playlistEntity.value = PlaylistEntity(playlistWithSongCount.value?.id ?: -1, playlistWithSongCount.value?.name ?: "")
            playlistWithSongCount.value = null
            showEditPlaylistBottomSheet.value = true
            showBottomSheet.value = false
        },
        onDelete = {
            deletePlayListWithCount.value = playlistWithSongCount.value
            playlistWithSongCount.value = null
            showDeleteAlert.value = true
            showBottomSheet.value = false
        },
        onDismiss = {
            playlistWithSongCount.value = null
            showBottomSheet.value = false
        }
    )


    when {
        lists.isNotEmpty() -> LazyVerticalGrid(
            modifier = listModifier,
            state = state,
            reverseLayout = false,
            columns = GridCells.Fixed(1)
        ) {
            items(count = filteredItems.size) { index ->
                val playlists = filteredItems[index]

                Card(
                    modifier = Modifier
                        .padding(
                            top = if (index == 0) Dimen.dimen3 else Dimen.dimen0,
                            bottom = Dimen.dimen20
                        )
                        .fillMaxWidth()
                        .wrapContentHeight()

                        .clickable(
                            enabled = true,
                            onClick = {
                               navController.navigate(
                                    Screens.Home.PlaylistSongs.route.replace(
                                        "{playlistEntity}",
                                        Gson().toJson(playlists)
                                    )
                                ) {
                                    popUpTo(Screens.Home.Dashboard.route) {
                                        inclusive = false
                                    }
                                }
                            }
                        ),
                    shape = RoundedCornerShape(Dimen.cornerShape),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ){
                    ConstraintLayout(
                        modifier = Modifier
                            .padding(
                                start = Dimen.dimen10,
                                top = Dimen.dimen10,
                                bottom = Dimen.dimen10
                            )
                            .fillMaxWidth()
                    ){
                        val (
                            refName,
                            refTotalSongs,
                            refMoreVertical) = createRefs()

                        IconButton(
                            modifier = Modifier
                                .constrainAs(refMoreVertical) {
                                    top.linkTo(parent.top)
                                    end.linkTo(parent.end)
                                    bottom.linkTo(parent.bottom)
                                },
                            onClick = {
                                playlistWithSongCount.value = playlists
                                showBottomSheet.value = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More vertical menu",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            modifier = Modifier
                                .constrainAs(refName) {
                                    start.linkTo(parent.start, Dimen.dimen10)
                                    top.linkTo(refMoreVertical.top, Dimen.dimen7)
                                    end.linkTo(refMoreVertical.start, Dimen.dimen20)
                                    bottom.linkTo(refTotalSongs.top)
                                    width = Dimension.fillToConstraints
                                    height = Dimension.fillToConstraints
                                },
                            text = playlists.name,
                            style = MaterialTheme.typography.titleLarge
                                .copy(color = MaterialTheme.colorScheme.onBackground),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            modifier = Modifier
                                .constrainAs(refTotalSongs) {
                                    start.linkTo(parent.start, Dimen.dimen10)
                                    top.linkTo(refName.bottom)
                                    end.linkTo(refMoreVertical.start, Dimen.dimen20)
                                    bottom.linkTo(refMoreVertical.bottom)
                                    width = Dimension.fillToConstraints
                                    height = Dimension.fillToConstraints
                                },
                            text = "${playlists.songCount} ${if (playlists.songCount > 1) "songs" else "song"}",
                            style = MaterialTheme.typography.bodyMedium
                                .copy(color = MaterialTheme.colorScheme.onTertiary)
                        )
                    }
                }
            }
        }

        else -> TextEmpty(emptyTextModifier)
    }
}