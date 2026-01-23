package com.example.gramotunes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music")
data class MusicEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val uri: String,
    val albumArtId: Long
)
