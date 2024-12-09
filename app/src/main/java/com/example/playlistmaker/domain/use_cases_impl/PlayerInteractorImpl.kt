package com.example.playlistmaker.domain.use_cases_impl

import com.example.playlistmaker.domain.PlayerListenerState
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.use_cases.PlayerInteractor

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

    override fun musicTimerFormat(time: Int): String {
        return playerRepository.musicTimerFormat(time)
    }

    override fun releaseMediaPlayer() {
        playerRepository.releaseMediaPlayer()
    }
}