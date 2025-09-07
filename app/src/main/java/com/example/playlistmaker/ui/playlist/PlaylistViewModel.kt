package com.example.playlistmaker.ui.playlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks

    private val _totalTime = MutableStateFlow("")
    val totalTime: StateFlow<String> = _totalTime

    private val _currentPlaylist = MutableStateFlow<Playlist?>(null)
    val currentPlaylist: StateFlow<Playlist?> = _currentPlaylist


    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            try {
                val playlist = playlistInteractor.getPlaylistById(playlistId)
                _currentPlaylist.value = playlist
            } catch (e: Exception) {
                Log.e("PlaylistViewModel", "Ошибка загрузки плейлиста", e)
            }
        }
    }

    fun loadTracksList(playlistId: String) {
        viewModelScope.launch {
            try {
                playlistInteractor.getTracksOfPlaylist(playlistId)
                    .collect { tracks ->
                        _tracks.value = tracks
                    }
            } catch (e: Exception) {
                Log.e("PlaylistViewModel", "Error loading tracks", e)
            }
        }
    }

    fun deleteTrackFromPlaylist(track: Track, playlistId: Int) {
        viewModelScope.launch {
            try {
                playlistInteractor.deleteTrackFromPlaylist(track, playlistId)
                loadTracksList(playlistId.toString())
            } catch (e: Exception) {
                Log.e("PlaylistViewModel", "Ошибка при удалении трека из плейлиста", e)
            }
        }
    }

    fun sharePlaylist(playlist: Playlist?) {
        if (playlist == null || playlist.numberOfTracks == 0) return
        viewModelScope.launch {
            try {
                sharingInteractor.sendPlaylist(playlist.id)
            } catch (e: Exception) {
                Log.e("PlaylistViewModel", "Ошибка отправки плейлиста", e)
            }
        }
    }

    fun deletePlaylist(playlist: Playlist?) {
        if (playlist == null) return
        viewModelScope.launch {
            try {
                playlistInteractor.deletePlaylist(playlist)
            } catch (e: Exception) {
                Log.e("PlaylistViewModel", "При удалении плейлиста произошла ошибка", e)
            }

        }
    }

//    fun getUpdatedPlaylist(playlistId: Int, callback: (Playlist?) -> Unit) {
//        viewModelScope.launch {
//            try {
//                val updatedPlaylist = playlistInteractor.getPlaylistById(playlistId.toInt())
//                callback(updatedPlaylist)
//            } catch (e: Exception) {
//                Log.e("PlaylistViewModel", "Ошибка при получении обновленного плейлиста", e)
//                callback(null)
//            }
//        }
//    }
}
