package com.example.playlistmaker.ui.mediatec.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.ui.mediatec.state.PlaylistState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PlaylistViewModel(val playlistInteractor: PlaylistInteractor) : ViewModel() {


    private val _statePlaylistScreen = MutableLiveData<PlaylistState>()
    val statePlaylistScreen: LiveData<PlaylistState> = _statePlaylistScreen

    init {
        loadPlaylist()
    }

    fun loadPlaylist() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .catch { error ->
                    _statePlaylistScreen.postValue(PlaylistState.Error(error.message.toString()))
                }
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        _statePlaylistScreen.postValue(PlaylistState.Empty)
                    } else {
                        _statePlaylistScreen.postValue(PlaylistState.Content(playlists))

                    }
                }
        }

    }
}