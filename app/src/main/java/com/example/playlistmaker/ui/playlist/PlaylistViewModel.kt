package com.example.playlistmaker.ui.playlist

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks

    private val _totalTime = MutableStateFlow("")
    val totalTime: StateFlow<String> = _totalTime

    fun loadTimeTracks(playlistId: String) {
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

    fun deleteTrackFromPlaylist(track: Track, playlist: Playlist?) {
        viewModelScope.launch {
            try {
                playlistInteractor.deleteTrackFromPlaylist(track, playlist)
                loadTimeTracks(playlist?.id.toString())
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

    fun getUpdatedPlaylist(playlistId: Int, callback: (Playlist?) -> Unit) {
        viewModelScope.launch {
            try {
                val updatedPlaylist = playlistInteractor.getPlaylistById(playlistId.toInt())
                callback(updatedPlaylist)
            } catch (e: Exception) {
                Log.e("PlaylistViewModel", "Ошибка при получении обновленного плейлиста", e)
                callback(null)
            }
        }
    }
}
