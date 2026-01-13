package com.example.gramotunes.domain.player

import android.net.Uri

interface MusicPlayer {
    fun play(uri: Uri)
    fun pause()
    fun stop()
    fun resume()
    fun release()
}