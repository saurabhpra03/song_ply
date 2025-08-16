package com.song.ply.presentation.utils

import android.os.Build
import androidx.annotation.RequiresApi

enum class NeededPermission(
    val permission: String,
    val title: String,
    val description: String,
    val permanentlyDeniedDescription: String
) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    READ_MEDIA_AUDIO(
        permission = android.Manifest.permission.READ_MEDIA_AUDIO,
        title = "Access Your Music Library",
        description = "To show and play songs stored on your device, we need access to your music library.",
        permanentlyDeniedDescription = "This permission is required to access your music files. You can enable it in the app settings.",
    ),

    READ_EXTERNAL_STORAGE(
        permission = android.Manifest.permission.READ_EXTERNAL_STORAGE,
        title = "Access Your Music Library",
        description = "To find and play songs on your device, please allow access to your music library.",
        permanentlyDeniedDescription = "This permission is required to access your music files. You can enable it in the app settings.",
    ),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    POST_NOTIFICATIONS(
        permission = android.Manifest.permission.POST_NOTIFICATIONS,
        title = "Notification Permission Needed",
        description = "To show audio playback controls while your music plays in the background, we need permission to send notifications.",
        permanentlyDeniedDescription = "Notification permission is required to show the mini player while music is playing. You can enable it in the app settings.",
    );

    fun permissionTextProvider(isPermanentDenied: Boolean): String {
        return if (isPermanentDenied) this.permanentlyDeniedDescription else this.description
    }
}

fun getNeededPermission(permission: String): NeededPermission =
    NeededPermission.entries.find { it.permission == permission }
        ?: throw IllegalArgumentException("Permission $permission is not supported")