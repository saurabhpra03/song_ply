package com.song.ply.presentation.utils

sealed class Resource<out T> {
    data object Loading: Resource<Nothing>()
    data class Success<out T>(val data: T): Resource<T>()
    data class Failed(val message: String): Resource<Nothing>()
}