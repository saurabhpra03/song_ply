package com.song.ply.framework.data.model

import android.net.Uri

data class Songs(
    var path: String?,
    var name: String?,
    var album: String?,
    var artist: String?,
    val duration: String?,
    val img: Uri?
)
