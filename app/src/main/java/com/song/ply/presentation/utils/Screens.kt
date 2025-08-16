package com.song.ply.presentation.utils

sealed class Screens(val route: String) {
    data object Home: Screens("nav_home"){
        data object Dashboard: Screens("dashboard")
        data object PlaylistSongs: Screens("playlistSongs/{playlistEntity}")
    }
}