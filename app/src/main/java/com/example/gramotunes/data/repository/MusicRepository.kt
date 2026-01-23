package com.example.gramotunes.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.gramotunes.data.local.dao.MusicDao
import com.example.gramotunes.data.model.MusicListModel
import com.example.gramotunes.mapper.MusicMapper.toMusicEntity
import com.example.gramotunes.mapper.MusicMapper.toMusicListModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MusicRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicDao: MusicDao
) {

    fun getMusicList(): Flow<List<MusicListModel>> {
        return musicDao.getAllMusic().map { list ->
            list.map { it.toMusicListModel() }
        }
    }

    suspend fun syncMusic() {
        if (musicDao.count() > 0) return
        musicDao.insertAll(getLocalMusic().map { it.toMusicEntity() })
    }

    fun getLocalMusic(): List<MusicListModel> {
        val musicList = mutableListOf<MusicListModel>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
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
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                )
                val albumId = cursor.getLong(albumIdCol)

                musicList.add(
                    MusicListModel(
                        id = id,
                        title = cursor.getString(titleCol),
                        artist = cursor.getString(artistCol),
                        duration = cursor.getLong(durationCol),
                        uri = uri,
                        albumId = albumId
                    )
                )
            }
        }

        return musicList
    }

}
