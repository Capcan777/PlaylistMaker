package com.example.playlistmaker.domain.use_case

interface PlayerInteractor {
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun playBackControl()
    fun musicTimer()
    fun releaseMediaPlayer()
    fun setOnPreparedListener(onPrepared: (Boolean) -> Unit)
}