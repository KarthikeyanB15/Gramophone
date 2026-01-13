package com.example.gramotunes.data.repository

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.example.gramotunes.data.MusicListModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MusicRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getLocalMusic(): List<MusicListModel> {
        val musicList = mutableListOf<MusicListModel>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->

            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                )

                musicList.add(
                    MusicListModel(
                        id = id,
                        title = cursor.getString(titleCol),
                        artist = cursor.getString(artistCol),
                        duration = cursor.getLong(durationCol),
                        uri = uri,
                        albumArt = getAlbumArt(uri)
                    )
                )
            }
        }

        return musicList
    }

    private fun getAlbumArt(uri: Uri): Bitmap? {
        return try {
            MediaMetadataRetriever().run {
                setDataSource(context, uri)
                embeddedPicture?.let {
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                }.also { release() }
            }
        } catch (_: Exception) {
            null
        }
    }
}
