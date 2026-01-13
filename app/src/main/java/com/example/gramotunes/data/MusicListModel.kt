package com.example.gramotunes.data

import android.graphics.Bitmap
import android.net.Uri

data class MusicListModel(
    val id: Long = 0L,
    val title: String = "",
    val artist: String = "",
    val duration: Long = 0L,
    val uri: Uri = Uri.EMPTY,
    val albumArt: Bitmap? = null
)
