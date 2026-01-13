package com.example.gramotunes.di

import android.content.Context
import com.example.gramotunes.data.player.ExoMusicPlayer
import com.example.gramotunes.domain.player.MusicPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    @Singleton
    fun provideMusicPlayer(
        @ApplicationContext context: Context
    ): MusicPlayer = ExoMusicPlayer(context)
}
