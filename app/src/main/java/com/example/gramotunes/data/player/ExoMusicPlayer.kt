package com.example.gramotunes.data.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.gramotunes.domain.player.MusicPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoMusicPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) : MusicPlayer {

    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(context.applicationContext).build()
    }

    override fun play(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun stop() {
        player.stop()
    }

    override fun resume() {
        player.play()
    }

    override fun release() {
        player.release()
    }
}
