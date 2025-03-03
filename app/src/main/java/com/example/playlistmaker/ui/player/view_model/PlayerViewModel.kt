package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.ui.player.state.PlaybackState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : ViewModel() {


    private var timerJob: Job? = null
    private var isPlaying = false

    val trackOnPlayer = historyInteractor.readTracksFromHistory()[0]

    private val trackInfoLiveData = MutableLiveData<Track>()
    fun getTrackInfoLiveData(): LiveData<Track> = trackInfoLiveData

    private val playbackTimeLiveData = MutableLiveData<String>()
    fun getPlaybackTimeLiveData(): LiveData<String> = playbackTimeLiveData

    private var playerStateLiveData = MutableLiveData(PlaybackState.DEFAULT_STATE)
    fun getPlayerStateLiveData(): LiveData<PlaybackState> = playerStateLiveData

    init {
        trackInfoLiveData.postValue(trackOnPlayer)
        preparePlayer()
    }

    private fun preparePlayer() {
        isPlaying = false
        playerStateLiveData.postValue(PlaybackState.PREPARED_STATE)
        playerInteractor.preparePlayer(
            trackOnPlayer.previewUrl,
            {
                playerStateLiveData.postValue(PlaybackState.PREPARED_STATE)
            },
            {
                playerStateLiveData.postValue(PlaybackState.PREPARED_STATE)
                playbackTimeLiveData.postValue(DEFAULT_TIME)
            })
    }

    private fun startPlayer() {
        isPlaying = true
        playerStateLiveData.postValue(PlaybackState.PLAYING_STATE)
        playerInteractor.startPlayer()
        postCurrentTime()
    }

    private fun pausePlayer() {
        isPlaying = false
        playerInteractor.pausePlayer()
        playerStateLiveData.postValue(PlaybackState.PAUSED_STATE)
    }

    fun playbackControl() {
        when (playerStateLiveData.value) {
            PlaybackState.PLAYING_STATE -> pausePlayer()
            PlaybackState.PAUSED_STATE, PlaybackState.PREPARED_STATE -> startPlayer()
            PlaybackState.DEFAULT_STATE -> {
                isPlaying = false
            }
            null -> {}
        }
    }

    fun onActivityPause() {
        if (playerStateLiveData.value == PlaybackState.PLAYING_STATE) pausePlayer()
    }

    private fun postCurrentTime() {
        timerJob = viewModelScope.launch {
            while (isPlaying) {
                playbackTimeLiveData.postValue(
                    if (playerStateLiveData.value == PlaybackState.PREPARED_STATE)
                        DEFAULT_TIME
                    else playerInteractor.getCurrentTime()
                )
                delay(TIMER_DELAY)
            }
        }
    }

    companion object {
        private const val TIMER_DELAY = 300L
        private const val DEFAULT_TIME = "00:00"
    }
}

