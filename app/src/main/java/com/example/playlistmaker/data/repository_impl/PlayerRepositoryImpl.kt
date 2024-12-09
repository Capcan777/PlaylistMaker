package com.example.playlistmaker.data.repository_impl

import android.media.MediaPlayer
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.domain.PlayerListenerState
import com.example.playlistmaker.domain.repository.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl() : PlayerRepository {

    private var mediaPlayer = MediaPlayer()
    private var playerState = Constants.STATE_DEFAULT
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }


    override fun preparePlayer(url: String, playerListenerState: PlayerListenerState) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = Constants.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerListenerState.playerOnStop()
            playerState = Constants.STATE_PREPARED
        }
    }

    override fun startPlayer(playerListenerState: PlayerListenerState) {
        mediaPlayer.start()
        playerListenerState.playerOnStart()
        playerState = Constants.STATE_PLAYING
    }

    override fun pausePlayer(playerListenerState: PlayerListenerState) {
        mediaPlayer.pause()
        playerListenerState.playerOnPause()
        playerState = Constants.STATE_PAUSED
    }

    override fun playBackControl(playerListenerState: PlayerListenerState) {
        when (playerState) {
            Constants.STATE_PLAYING -> {
                pausePlayer(playerListenerState)

            }

            Constants.STATE_PREPARED, Constants.STATE_PAUSED -> {
                startPlayer(playerListenerState)
            }
        }
    }

    override fun musicTimerFormat(time: Int): String {
        if (playerState == Constants.STATE_PLAYING) {
            return dateFormat.format(mediaPlayer.currentPosition)
        } else {
            return dateFormat.format(0)
        }

    }

    override fun releaseMediaPlayer() {
        mediaPlayer.release()
    }
}