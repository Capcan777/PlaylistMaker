package com.example.playlistmaker.data.repository_impl

import android.media.MediaPlayer
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.repository.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl : PlayerRepository {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
        }
    }

    override fun setOnPreparedListener(onPrepared: (Boolean) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun setOnCompletionListener(onCompletion: (Boolean) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        TODO("Not yet implemented")
    }

    override fun playBackControl() {
        TODO("Not yet implemented")
    }

//    override fun musicTimer() {
//        TODO("Not yet implemented")
//    }

    override fun releaseMediaPlayer() {
        TODO("Not yet implemented")
    }

}