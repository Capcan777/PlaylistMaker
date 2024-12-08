package com.example.playlistmaker.domain.repository

interface PlayerRepository {
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun playBackControl()
    fun musicTimerFormat()
    fun releaseMediaPlayer()
}