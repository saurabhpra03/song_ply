package com.song.ply.presentation.ui.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.song.ply.framework.database.entity.PlaylistEntity
import com.song.ply.presentation.ui.theme.SongPlyTheme
import com.song.ply.presentation.ui.view.playlistSongs.PlaylistSongsScreen
import com.song.ply.presentation.ui.viewModel.PlayerManagerViewModel
import com.song.ply.presentation.ui.viewModel.SongsViewModel
import com.song.ply.presentation.utils.Screens

@Composable
fun MainNavHost(
    activity: MainActivity,
    viewModel: SongsViewModel = hiltViewModel(),
    playerManagerViewModel: PlayerManagerViewModel = hiltViewModel()
) {

    val navController = rememberNavController()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    // Controls visibility of MiniPlayer
    val showMiniPlayer = rememberSaveable { mutableStateOf(false) }
    val selectedSongPath = rememberSaveable { mutableStateOf("") }


    SongPlyTheme(
        darkTheme = isDarkTheme
    ) {
        Scaffold(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            bottomBar = {
                MiniPlayer(
                    showMiniPlayer = showMiniPlayer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    playerManagerViewModel = playerManagerViewModel,
                    onClose = {
                        playerManagerViewModel.clearPlayer()
                        showMiniPlayer.value = false
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screens.Home.Dashboard.route,
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                composable(Screens.Home.Dashboard.route) {
                    PermissionsScreen(
                        activity,
                        navController,
                        showMiniPlayer,
                        selectedSongPath,
                        viewModel,
                        playerManagerViewModel
                    )
                }

                composable(Screens.Home.PlaylistSongs.route) {
                    val jsonData = it.arguments?.getString("playlistEntity") ?: ""
                    val playlistEntity = Gson().fromJson(jsonData, PlaylistEntity::class.java)
                    PlaylistSongsScreen(
                        navController,
                        playlistEntity,
                        showMiniPlayer,
                        selectedSongPath,
                        playerManagerViewModel
                    )
                }

            }
        }
    }
}