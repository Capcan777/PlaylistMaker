package com.example.playlistmaker.domain.use_cases

interface PlayerInteractor {
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun playBackControl()
    fun musicTimerFormat()
    fun releaseMediaPlayer()
}