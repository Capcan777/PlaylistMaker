package com.example.playlistmaker.domain.use_cases_impl

import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.use_cases.PlayerInteractor

class PlayerInteractorImpl(val playerRepository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(url: String) {
        playerRepository.preparePlayer(url)
    }

    override fun startPlayer() {
        playerRepository.startPlayer()
    }

    override fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    override fun playBackControl() {
        playerRepository.playBackControl()
    }

    override fun musicTimerFormat() {
        playerRepository.musicTimerFormat()
    }

    override fun releaseMediaPlayer() {
        playerRepository.releaseMediaPlayer()
    }
}