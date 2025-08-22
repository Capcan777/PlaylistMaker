package com.example.playlistmaker.ui.player.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorite.FavoriteInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.ui.mediatec.state.PlaylistState
import com.example.playlistmaker.ui.player.state.PlayerPlaylistState
import com.example.playlistmaker.ui.player.state.PlayerScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val historyInteractor: SearchHistoryInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var timerJob: Job? = null
    private var isPlaying = false

    val trackOnPlayer = historyInteractor.readTracksFromHistory()[0]

    private var trackInfoLiveData = MutableLiveData<Track?>()
    fun getTrackInfoLiveData(): LiveData<Track?> = trackInfoLiveData

    private val playbackTimeLiveData = MutableLiveData<String>()
    fun getPlaybackTimeLiveData(): LiveData<String> = playbackTimeLiveData

    private var playerStateLiveData = MutableLiveData(PlayerScreenState.DEFAULT_STATE)
    fun getPlayerStateLiveData(): LiveData<PlayerScreenState> = playerStateLiveData

    private var _playerPlaylistState = MutableLiveData<PlayerPlaylistState>()
    val playerPlaylistState: LiveData<PlayerPlaylistState> = _playerPlaylistState

    init {
        loadPlaylists()
    }


    private fun preparePlayer(track: Track) {
        isPlaying = false
        playerStateLiveData.postValue(PlayerScreenState.PREPARED_STATE)
        playerInteractor.preparePlayer(
            track.previewUrl,
            {
                playerStateLiveData.postValue(PlayerScreenState.PREPARED_STATE)
            },
            {
                playerStateLiveData.postValue(PlayerScreenState.PREPARED_STATE)
                playbackTimeLiveData.postValue(DEFAULT_TIME)
            })
    }

    private fun startPlayer() {
        isPlaying = true
        playerStateLiveData.postValue(PlayerScreenState.PLAYING_STATE)
        playerInteractor.startPlayer()
        postCurrentTime()
    }

    private fun pausePlayer() {
        isPlaying = false
        playerInteractor.pausePlayer()
        playerStateLiveData.postValue(PlayerScreenState.PAUSED_STATE)
    }

    fun playbackControl() {
        when (playerStateLiveData.value) {
            PlayerScreenState.PLAYING_STATE -> pausePlayer()
            PlayerScreenState.PAUSED_STATE, PlayerScreenState.PREPARED_STATE -> startPlayer()
            PlayerScreenState.DEFAULT_STATE -> {
                isPlaying = false
            }

            null -> {}
        }
    }

    fun loadTrackState() {
        viewModelScope.launch {
                val track = trackInfoLiveData.value
                if (track != null) {
                    val isFavorite = favoriteInteractor.inFavorite(track.trackId)
                    track.isFavorite = isFavorite
                    trackInfoLiveData.postValue(track)
                }
        }
    }


fun onActivityPause() {
    if (playerStateLiveData.value == PlayerScreenState.PLAYING_STATE) pausePlayer()
}

private fun postCurrentTime() {
    timerJob = viewModelScope.launch {
        while (isPlaying) {
            playbackTimeLiveData.postValue(
                if (playerStateLiveData.value == PlayerScreenState.PREPARED_STATE)
                    DEFAULT_TIME
                else playerInteractor.getCurrentTime()
            )
            delay(TIMER_DELAY)
        }
    }
}


fun onFavoriteClicked() {
    viewModelScope.launch {
        val track = trackInfoLiveData.value
        if (track != null) {
            if (track.isFavorite) {
                track.isFavorite = false
                trackInfoLiveData.postValue(track)
                viewModelScope.launch {
                    favoriteInteractor.removeTrackFromFavorite(track)
                }
            } else {
                track.isFavorite = true
                trackInfoLiveData.postValue(track)
                viewModelScope.launch {
                    favoriteInteractor.addTrackToFavorite(track)
                }
            }
        }
    }
}

suspend fun checkIsFavorite(trackId: Int): Boolean {
    return favoriteInteractor.inFavorite(trackId)
}

fun updateTrackFavoriteStatus(trackId: Int, isFavorite: Boolean) {
    val track = trackInfoLiveData.value
    if (track != null && track.trackId == trackId) {
        track.isFavorite = isFavorite
        trackInfoLiveData.postValue(track)
    }
}

    fun setTrack(track: Track) {
        trackInfoLiveData.value = track
        preparePlayer(track)
        loadTrackState()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .catch { error ->
                    _playerPlaylistState.postValue(PlayerPlaylistState.Error(error.message.toString()))
                }
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        _playerPlaylistState.postValue(PlayerPlaylistState.Empty)
                    } else {
                        _playerPlaylistState.postValue(PlayerPlaylistState.Content(playlists))
                    }
                }
        }
    }


companion object {
    private const val TIMER_DELAY = 300L
    private const val DEFAULT_TIME = "00:00"
}
}


