package com.song.ply.presentation.ui.view.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.song.ply.R
import com.song.ply.framework.data.model.Songs
import com.song.ply.presentation.ui.theme.Dimen
import com.song.ply.presentation.ui.view.common.DialogDeleteAlert
import com.song.ply.presentation.ui.view.common.HeaderIcon
import com.song.ply.presentation.ui.view.common.SearchField
import com.song.ply.presentation.ui.view.playlist.PlaylistScreen
import com.song.ply.presentation.ui.view.songs.SongsScreen
import com.song.ply.presentation.ui.viewModel.PlayerManagerViewModel
import com.song.ply.presentation.ui.viewModel.SongsViewModel
import com.song.ply.presentation.utils.Const.toast

@Composable
fun HomeScreen(
    context: Context,
    navController: NavController,
    showMiniPlayer: MutableState<Boolean>,
    selectedSongPath: MutableState<String>,
    songsViewModel: SongsViewModel,
    playerManagerViewModel: PlayerManagerViewModel
) {

    // Search state
    val searchText = remember { mutableStateOf("") }
    val isSearch = remember { mutableStateOf(false) }

    // Selected tab index
    val selectedTab = remember { mutableIntStateOf(0) }


    // "Add to Playlist" bottom sheet
    val showPlaylistBottomSheet = remember { mutableStateOf(false) }
    val playlistAudioData = remember { mutableStateOf<Songs?>(null) }

    // Delete confirmation dialog
    val showDeleteAlert = remember { mutableStateOf(false) }
    val deleteSong = remember { mutableStateOf<Songs?>(null) }

    // Handles result from MediaStore delete intent (Android 11+)
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { songsViewModel.fetchSongs() }

    // "More Options" bottom sheet
    val showBottomSheet = remember { mutableStateOf(false) }
    val songs = remember { mutableStateOf<Songs?>(null) }

    BottomSheetMoreOptions(
        context = context,
        showBottomSheet = showBottomSheet,
        songs = songs,
        songsViewModel = songsViewModel,
        playerManagerViewModel = playerManagerViewModel,
        onAddToPlaylist = { song ->
            playlistAudioData.value = song
            showPlaylistBottomSheet.value = true
        },
        onDelete = { song ->
            if (song.path == playerManagerViewModel.currentSong.value?.path) {
                context.toast(context.getString(R.string.song_is_playing))
            } else {
                deleteSong.value = song
                showDeleteAlert.value = true
            }
        }
    )

    // Permission launcher for deleting files on API < 30
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            deleteSong.value?.let { song ->
                songsViewModel.deleteSong(context, song.path ?: "", launcher)
            }
        } else {
            context.toast(
                context.getString(R.string.allow_storage_permission_to_delete_this_song)
            )
        }
    }

    DialogDeleteAlert(
        showDeleteFileAlert = showDeleteAlert,
        title = stringResource(R.string.delete_audio),
        fileName = deleteSong.value?.name ?: "Unknown",
        onClickDelete = {
            showDeleteAlert.value = false
            val path = deleteSong.value?.path ?: ""

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                // Request WRITE_EXTERNAL_STORAGE if needed
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    songsViewModel.deleteSong(context, path, launcher)
                }
            } else {
                // API >= 30: handled via IntentSender
                songsViewModel.deleteSong(context, path, launcher)
            }
        }
    )

    // Show "Add to Playlist" bottom sheet if required
    playlistAudioData.value?.let { audioData ->
        if (showPlaylistBottomSheet.value) {
            BottomSheetAddToPlayList(
                showPlayListBottomSheet = showPlaylistBottomSheet,
                songs = audioData
            )
        }
    }


    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (
            refTitle,
            refSearch,
            refTheme,
            refTab,
            refAudioList,
            refEmptyText) = createRefs()

        val listModifier = Modifier
            .constrainAs(refAudioList) {
                start.linkTo(parent.start, Dimen.screenPadding)
                top.linkTo(refTab.bottom, Dimen.screenPadding)
                end.linkTo(parent.end, Dimen.screenPadding)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }

        val emptyTextModifier = Modifier
            .constrainAs(refEmptyText) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }


        AnimatedVisibility(
            modifier = Modifier
                .constrainAs(refTitle) {
                    start.linkTo(parent.start, Dimen.screenPadding)
                    top.linkTo(parent.top, Dimen.screenPadding)
                },
            visible = !isSearch.value,
            content = {
                Text(
                    text = stringResource(R.string.app_name),
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        )

        AnimatedVisibility(
            modifier = Modifier
                .constrainAs(refSearch) {
                    start.linkTo(parent.start, Dimen.dimen20)
                    top.linkTo(parent.top, Dimen.dimen20)
                    end.linkTo(refTheme.start, Dimen.dimen30)
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
                        songsViewModel.onSearchTextChange(searchText.value)
                    }
                )
            }
        )

        HeaderIcon(
            modifier = Modifier
                .constrainAs(refTheme) {
                    top.linkTo(parent.top, Dimen.dimen20)
                    end.linkTo(parent.end, Dimen.dimen20)
                },
            icon = if (isSearch.value) Icons.Rounded.Close else Icons.Rounded.Search,
            onClick = { isSearch.value = !isSearch.value }
        )

        AppTab(
            modifier = Modifier
                .constrainAs(refTab) {
                    start.linkTo(parent.start)
                    top.linkTo(refTheme.bottom, Dimen.dimen20)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .wrapContentHeight(),
            selectedTab = selectedTab)

        when (selectedTab.intValue) {
            0 -> {
                songsViewModel.onSearchTextChange("")
                searchText.value = ""

                SongsScreen(
                    listModifier = listModifier,
                    emptyTextModifier = emptyTextModifier,
                    showMiniPlayer = showMiniPlayer,
                    selectedSongPath = selectedSongPath,
                    playerManagerViewModel = playerManagerViewModel,
                    onMoreClick = { data ->
                        songs.value = data
                        showBottomSheet.value = true
                    },
                    songsViewModel = songsViewModel
                )
            }

            else -> {
                songsViewModel.onSearchTextChange("")
                searchText.value = ""

                PlaylistScreen(
                    listModifier = listModifier,
                    emptyTextModifier = emptyTextModifier,
                    navController = navController,
                    songsViewModel = songsViewModel
                )
            }
        }
    }
}