package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.domain.favorite.FavoriteInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.ui.player.state.PlayerScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val historyInteractor: SearchHistoryInteractor,
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {


    private var timerJob: Job? = null
    private var isPlaying = false
    private var isFavorite = false

    val trackOnPlayer = historyInteractor.readTracksFromHistory()[0]

    private val trackInfoLiveData = MutableLiveData<Track?>()
    fun getTrackInfoLiveData(): LiveData<Track?> = trackInfoLiveData

    private val playbackTimeLiveData = MutableLiveData<String>()
    fun getPlaybackTimeLiveData(): LiveData<String> = playbackTimeLiveData

    private var playerStateLiveData = MutableLiveData(PlayerScreenState.DEFAULT_STATE)
    fun getPlayerStateLiveData(): LiveData<PlayerScreenState> = playerStateLiveData

//    private val favoriteState = MutableLiveData(isFavorite)
//    fun getFavoriteTrackLiveData(): LiveData<Boolean> = favoriteState


    init {
        trackInfoLiveData.postValue(trackOnPlayer)
        preparePlayer()
        loadTrackState()
    }

    private fun preparePlayer() {
        isPlaying = false
        playerStateLiveData.postValue(PlayerScreenState.PREPARED_STATE)
        playerInteractor.preparePlayer(
            trackOnPlayer.previewUrl,
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

    private fun loadTrackState() { /// не доконца реализовал
        viewModelScope.launch {
            val track = trackInfoLiveData.value
            if (track != null) {
                track.isFavorite = favoriteInteractor.inFavorite(track.trackId)
                trackInfoLiveData.postValue(track)
            }
        }
    }


    fun onFavoriteClicked() {       // реализовать
        viewModelScope.launch {
            val track = trackInfoLiveData.value
            if (track != null) {
                if (track.isFavorite) {
                    favoriteInteractor.removeTrackFromFavorite(track)
                    trackInfoLiveData.postValue(track.apply { isFavorite = false })
                } else {
                    favoriteInteractor.addTrackToFavorite(track)
                    trackInfoLiveData.postValue(track.apply { isFavorite = true })
                }
                loadTrackState()
            }

        }
    }


    companion object {
        private const val TIMER_DELAY = 300L
        private const val DEFAULT_TIME = "00:00"
    }
}


