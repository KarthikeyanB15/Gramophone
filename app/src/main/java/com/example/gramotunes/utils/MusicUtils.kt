package com.example.gramotunes.utils

import android.content.ContentUris
import android.net.Uri
import androidx.core.net.toUri

object MusicUtils {

    fun getAlbumArtUri(albumId: Long): Uri =
        ContentUris.withAppendedId(
            "content://media/external/audio/albumart".toUri(),
            albumId
        )

}