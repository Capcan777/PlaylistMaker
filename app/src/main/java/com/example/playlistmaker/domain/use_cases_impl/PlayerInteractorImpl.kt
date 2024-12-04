package com.example.playlistmaker.domain.use_cases_impl

import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.use_case.PlayerInteractor

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(url: String) {
        playerRepository.preparePlayer(url)
    }

    override fun startPlayer() {
        TODO("Not yet implemented")
    }

    override fun pausePlayer() {
        TODO("Not yet implemented")
    }

    override fun playBackControl() {
        TODO("Not yet implemented")
    }

    override fun musicTimer() {
        TODO("Not yet implemented")
    }

    override fun releaseMediaPlayer() {
        TODO("Not yet implemented")
    }

    override fun setOnPreparedListener(onPrepared: (Boolean) -> Unit) {
        TODO("Not yet implemented")
    }

}