package com.example.gramotunes.di

import android.content.Context
import androidx.room.Room
import com.example.gramotunes.data.local.db.GramoTunesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GramoTunesDatabase =
        Room.databaseBuilder(
            context,
            GramoTunesDatabase::class.java,
            "gramo-tunes-database"
        ).build()

    @Provides
    @Singleton
    fun providePlaylistDao(database: GramoTunesDatabase) = database.musicDao()
}