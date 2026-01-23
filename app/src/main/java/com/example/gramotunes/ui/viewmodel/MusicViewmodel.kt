package com.example.gramotunes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gramotunes.data.model.MusicListModel
import com.example.gramotunes.data.repository.MusicRepository
import com.example.gramotunes.domain.player.MusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewmodel @Inject constructor(
    private val repository: MusicRepository,
    private val musicPlayer: MusicPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicListModel())
    val uiState: StateFlow<MusicListModel> = _uiState

    private val _showMiniPlayer = MutableStateFlow(false)
    val showMiniPlayer: StateFlow<Boolean> = _showMiniPlayer

    fun loadMusic() {
        viewModelScope.launch {
            repository.syncMusic()
        }
    }

    val musicList: StateFlow<List<MusicListModel>> =
        repository.getMusicList()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun play(musicItem: MusicListModel) {
        _uiState.value = musicItem
        _showMiniPlayer.value = true
        musicPlayer.play(musicItem.uri)
    }

    fun pause() {
        musicPlayer.pause()
    }

    fun resume() {
        musicPlayer.resume()
    }

}