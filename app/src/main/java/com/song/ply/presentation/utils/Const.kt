package com.song.ply.presentation.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlin.math.log10
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever

object Const {

    fun String.logD(message: String) = Log.d(this, message)

    fun String.logE(message: String) = Log.e(this, message)

    fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    // Convert audio file size length
    fun Long.toReadableFileSize(): String {
        if (this <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(this.toDouble()) / log10(1000.0)).toInt()
        return String.format("%.2f %s", this / Math.pow(1000.0, digitGroups.toDouble()), units[digitGroups])
    }

    // Convert to audio duration
    fun Long.formatDuration(): String {
        val totalSeconds = this / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Extension to check permission
    fun Context.hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    // Extension to open app settings
    fun Activity.goToAppSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).also {
            startActivity(it)
        }
    }

    fun Context.getAudioThumbnail(audioUri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        var thumbnailBitmap: Bitmap? = null
        try {
            retriever.setDataSource(this, audioUri)
            val embeddedPicture = retriever.embeddedPicture // Get the album art as a byte array
            if (embeddedPicture != null) {
                thumbnailBitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return thumbnailBitmap
    }



}