package com.example.playlistmaker.domain.repository

interface PlayerRepository {
    fun preparePlayer(url: String)
    fun setOnPreparedListener(onPrepared: (Boolean) -> Unit)
    fun setOnCompletionListener(onCompletion: (Boolean) -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun playBackControl()
    fun musicTimer()
    fun releaseMediaPlayer()
}