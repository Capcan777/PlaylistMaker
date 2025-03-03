package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.player.PlayerInteractor

class PlayerInteractorImpl(val playerRepository: PlayerRepository) : PlayerInteractor {

    override fun preparePlayer(
        trackUrl: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    ) {
        playerRepository.preparePlayer(
            trackUrl,
            onPreparedListener,
            onCompletionListener
        )
    }

    override fun startPlayer() {
        playerRepository.startPlayer()
    }

    override fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    override fun getCurrentTime(): String {
        return playerRepository.getCurrentTime()
    }

    override fun releaseMediaPlayer() {
        playerRepository.releaseMediaPlayer()
    }
}