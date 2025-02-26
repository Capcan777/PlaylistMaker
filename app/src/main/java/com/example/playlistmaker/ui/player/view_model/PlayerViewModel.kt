package com.example.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.PlayerListenerState
import com.example.playlistmaker.domain.model.Track

class PlayerViewModel(private val track: Track) : ViewModel(), PlayerListenerState {
    private val playerInteractor = Creator.providePlayerInteractor()

    private val _playButtonRes = MutableLiveData<Int>()
    val playButtonRes: LiveData<Int> = _playButtonRes

    private val _musicTimer = MutableLiveData<String>()
    val musicTimer: LiveData<String> = _musicTimer


    private var handler: Handler? = null

    companion object {
        fun provideFactory(track: Track) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return PlayerViewModel(track) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    init {
        preparePlayer(track.previewUrl)
    }

    ////
////        val track = Gson().fromJson(
////            intent.getStringExtra(Constants.TRACK_INTENT),
////            Track::class.java
////        )
////        val url = track.previewUrl
//
////        binding.apply {
////            fallBack.setOnClickListener { finish() }
////            ibPlay.setOnClickListener { playerInteractor.playBackControl(this@PlayerActivity) }
////            tvTrackName.text = track.trackName
////            tvArtistName.text = track.artistName
////            timingTrack.text = playerInteractor.musicTimerFormat(0)
////            countOfTrack.text = playerInteractor.musicTimerFormat(track.trackTimeMillis)
////            dataTrack.text = track.releaseDate.slice(0..3)
////            styleTrack.text = track.primaryGenreName
////            countryTrack.text = track.country
////
////            Glide.with(applicationContext)
////                .load(track.getCoverArtwork())
////                .centerCrop()
////                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.radiusCorner_8)))
////                .placeholder(R.drawable.poster_placeholder)
////                .into(ivPoster)
////
////            if (track.collectionName.isNotEmpty()) {
////                albumTrack.text = track.collectionName
////            } else {
////                tvAlbumGroup.isVisible = false
////            }
////        }
//
//        playerInteractor.preparePlayer(url, this)
//    }
//
//     fun onPause() {
//        playerInteractor.pausePlayer(this)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        playerInteractor.releaseMediaPlayer()
//    }
//
//    private fun updateMusicTimer() {
//        handler.postDelayed(
//            object : Runnable {
//                override fun run() {
//                    if (playerInteractor.musicTimerFormat(0) != null) {
//                        binding.timingTrack.text = playerInteractor.musicTimerFormat(0)
//                        handler.postDelayed(this, Constants.TIME_UPDATE_DELAY)
//                    }
//                }
//            }, Constants.TIME_UPDATE_DELAY
//        )
//    }
    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(url, this)
    }

    fun playPause() {
        playerInteractor.playBackControl(this)
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer(this)
    }

    // Метод для форматирования длительности трека (используется для общего времени)
    fun formatDuration(milliseconds: Int): String {
        return playerInteractor.musicTimerFormat(milliseconds)
    }

    fun getTrackInfo(): Track = track

    // ----------------- Реализация PlayerListenerState -----------------

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

    // ----------------- Таймер обновления -----------------

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
