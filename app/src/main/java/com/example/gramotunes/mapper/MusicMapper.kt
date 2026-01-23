package com.example.gramotunes.mapper

import androidx.core.net.toUri
import com.example.gramotunes.data.local.entity.MusicEntity
import com.example.gramotunes.data.model.MusicListModel

object MusicMapper {

    fun MusicListModel.toMusicEntity(): MusicEntity {
        return MusicEntity(
            id = id,
            title = title,
            artist = artist,
            duration = duration,
            uri = uri.toString(),
            albumArtId = albumId
        )
    }

    fun MusicEntity.toMusicListModel() : MusicListModel {
        return MusicListModel(
            id = id,
            title = title,
            artist = artist,
            duration = duration,
            uri = uri.toUri(),
            albumId = albumArtId
        )
    }
}