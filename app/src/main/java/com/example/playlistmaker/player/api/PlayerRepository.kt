package com.example.playlistmaker.player.api

import com.example.playlistmaker.domain.PlayerListenerState

interface PlayerRepository {
    fun preparePlayer(url: String, playerListenerState: PlayerListenerState)
    fun startPlayer(playerListenerState: PlayerListenerState)
    fun pausePlayer(playerListenerState: PlayerListenerState)
    fun playBackControl(playerListenerState: PlayerListenerState)
    fun musicTimerFormater(time: Int): String
    fun releaseMediaPlayer()
}