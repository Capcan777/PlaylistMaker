package com.example.playlistmaker.data.player.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(private var mediaPlayer: MediaPlayer) : PlayerRepository {

    //    private var mediaPlayer = MediaPlayer()
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }


    override fun preparePlayer(
        trackUrl: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    ) {
        if (trackUrl != "No previewUrl") {
            mediaPlayer = mediaPlayer.apply {
                setDataSource(trackUrl)
                prepareAsync()
                setOnPreparedListener { onPreparedListener() }
                setOnCompletionListener { onCompletionListener() }
            }
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun getCurrentTime(): String {
        return dateFormat.format(mediaPlayer.currentPosition)
    }

    override fun releaseMediaPlayer() {
        mediaPlayer.release()
    }
}