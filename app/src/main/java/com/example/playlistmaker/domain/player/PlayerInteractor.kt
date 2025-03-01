package com.example.playlistmaker.domain.player

interface PlayerInteractor {
    fun preparePlayer(url: String, playerListenerState: PlayerListenerState)
    fun startPlayer(playerListenerState: PlayerListenerState)
    fun pausePlayer(playerListenerState: PlayerListenerState)
    fun playBackControl(playerListenerState: PlayerListenerState)
    fun musicTimerFormat(time: Long): String
    fun releaseMediaPlayer()
    fun getFormatTrackTime(milliseconds: Long): String
}