package com.example.playlistmaker.ui.playlist

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class PlaylistViewModel(val playlistInteractor: PlaylistInteractor) : ViewModel() {

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
}
