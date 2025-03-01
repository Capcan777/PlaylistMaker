package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.PlayerListenerState
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.player.PlayerInteractor

class PlayerInteractorImpl(val playerRepository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(url: String, playerListenerState: PlayerListenerState) {
        playerRepository.preparePlayer(url, playerListenerState)
    }

    override fun startPlayer(playerListenerState: PlayerListenerState) {
        playerRepository.startPlayer(playerListenerState)
    }

    override fun pausePlayer(playerListenerState: PlayerListenerState) {
        playerRepository.pausePlayer(playerListenerState)
    }

    override fun playBackControl(playerListenerState: PlayerListenerState) {
        playerRepository.playBackControl(playerListenerState)
    }

    override fun musicTimerFormat(time: Long): String {
        return playerRepository.musicTimerFormat(time)
    }

    override fun releaseMediaPlayer() {
        playerRepository.releaseMediaPlayer()
    }

    override fun getFormatTrackTime(milliseconds: Long): String {
        return playerRepository.getFormatTrackTime(milliseconds)
    }
}