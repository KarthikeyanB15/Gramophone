package com.example.gramotunes.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gramotunes.data.MusicListModel
import com.example.gramotunes.data.repository.MusicRepository
import com.example.gramotunes.domain.player.MusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MusicViewmodel @Inject constructor(
    private val repository: MusicRepository,
    private val musicPlayer: MusicPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicListModel())
    val uiState: StateFlow<MusicListModel> = _uiState

    fun getLocalMusic(): List<MusicListModel> {
        return repository.getLocalMusic()
    }

    fun play(musicItem: MusicListModel) {
        _uiState.value = musicItem
        musicPlayer.play(musicItem.uri)
    }

    fun pause() {
        musicPlayer.pause()
    }

    fun resume() {
        musicPlayer.resume()
    }

}