package com.example.playlistmaker.service

import kotlinx.coroutines.flow.StateFlow

interface PlayerServiceApi {
    val playerState: StateFlow<PlayerUiState>

    fun play()
    fun pause()
    fun startForeground()
    fun stopForeground()

    fun setTrackInfo(trackName: String, artistName: String, previewUrl: String)

    // Заполняется при onBind() в сервисе
    data class BoundTrackInfo(
        val trackName: String,
        val artistName: String,
        val previewUrl: String
    )
}

sealed interface PlayerUiState {
    object Default: PlayerUiState
    object Prepared: PlayerUiState
    object Playing: PlayerUiState
    object Paused: PlayerUiState
    data class Time(val formatted: String): PlayerUiState
}



