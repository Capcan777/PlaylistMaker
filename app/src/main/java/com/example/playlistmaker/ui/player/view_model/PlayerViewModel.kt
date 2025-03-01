package com.example.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.domain.player.PlayerListenerState
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.player.PlayerInteractor

class PlayerViewModel(private val track: Track, private val playerInteractor: PlayerInteractor) :
    ViewModel(), PlayerListenerState {

    private val _playButtonRes = MutableLiveData<Int>()
    val playButtonRes: LiveData<Int> = _playButtonRes

    private val _musicTimer = MutableLiveData<String>()
    val musicTimer: LiveData<String> = _musicTimer

    private val _trackDuration = MutableLiveData<String>()
    val trackDuration: LiveData<String> = _trackDuration

    init {
        _trackDuration.value = formatDuration(track.trackTimeMillis)
    }


    private var handler: Handler? = null

    companion object {
        fun provideFactory(track: Track, playerInteractor: PlayerInteractor) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return PlayerViewModel(track, playerInteractor) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }

    init {
        preparePlayer(track.previewUrl)
    }

    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(url, this)
    }

    fun playPause() {
        playerInteractor.playBackControl(this)
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer(this)
    }

    fun formatDuration(milliseconds: Long): String {
        return playerInteractor.getFormatTrackTime(milliseconds)
    }

    fun getTrackInfo(): Track = track

    override fun playerOnStart() {
        _playButtonRes.postValue(R.drawable.ic_pause_button)
        startTimer()
    }

    override fun playerOnStop() {
        _playButtonRes.postValue(R.drawable.ic_play_button)
        stopTimer()
    }

    override fun playerOnPause() {
        _playButtonRes.postValue(R.drawable.ic_play_button)
        stopTimer()
    }


    private fun startTimer() {
        handler = Handler(Looper.getMainLooper())
        handler?.postDelayed(object : Runnable {
            override fun run() {
                val timerText = playerInteractor.musicTimerFormat(0)
                if (timerText != null) {
                    _musicTimer.postValue(timerText)
                    handler?.postDelayed(this, Constants.TIME_UPDATE_DELAY)
                }
            }
        }, Constants.TIME_UPDATE_DELAY)
    }

    private fun stopTimer() {
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releaseMediaPlayer()
    }
}
