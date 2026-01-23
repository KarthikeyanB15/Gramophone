package com.example.gramotunes.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gramotunes.data.local.dao.MusicDao
import com.example.gramotunes.data.local.entity.MusicEntity

@Database(entities = [MusicEntity::class], version = 1)
abstract class GramoTunesDatabase: RoomDatabase() {
    abstract fun musicDao(): MusicDao
}