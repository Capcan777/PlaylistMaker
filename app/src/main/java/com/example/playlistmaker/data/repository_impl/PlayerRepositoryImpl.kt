package com.example.playlistmaker.data.repository_impl

import android.media.MediaPlayer
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.domain.repository.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl() : PlayerRepository {

    private var mediaPlayer = MediaPlayer()
    private var playerState = Constants.STATE_DEFAULT
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }


    override fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = Constants.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = Constants.STATE_PREPARED
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = Constants.STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = Constants.STATE_PAUSED
    }

    override fun playBackControl() {
        when (playerState) {
            Constants.STATE_PLAYING -> {
                pausePlayer()
            }

            Constants.STATE_PREPARED, Constants.STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun musicTimerFormat() {
        dateFormat.format(mediaPlayer.currentPosition)
//        handler.postDelayed(
//            object : Runnable {
//                override fun run() {
//                    if (playerState == Constants.STATE_PLAYING) {
//                        binding.timingTrack.text = formatTime(mediaPlayer.currentPosition)
//                        handler.postDelayed(this, Constants.TIME_UPDATE_DELAY)
//                    } else if (playerState == Constants.STATE_PREPARED) {
//                        binding.timingTrack.text = formatTime(0)
//                    }
//                }
//
//            }, Constants.TIME_UPDATE_DELAY
//        )
    }

    override fun releaseMediaPlayer() {
        mediaPlayer.release()
    }
}