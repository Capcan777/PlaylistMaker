package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.PlayerListenerState

interface PlayerRepository {
    fun preparePlayer(url: String, playerListenerState: PlayerListenerState)
    fun startPlayer(playerListenerState: PlayerListenerState)
    fun pausePlayer(playerListenerState: PlayerListenerState)
    fun playBackControl(playerListenerState: PlayerListenerState)
    fun musicTimerFormat(time: Int): String
    fun releaseMediaPlayer()
}