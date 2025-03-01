package com.example.playlistmaker.domain.player

interface PlayerListenerState {
    fun playerOnStart()
    fun playerOnStop()
    fun playerOnPause()
}