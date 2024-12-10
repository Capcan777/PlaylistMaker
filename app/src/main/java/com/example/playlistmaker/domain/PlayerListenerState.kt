package com.example.playlistmaker.domain

interface PlayerListenerState {
    fun playerOnStart()
    fun playerOnStop()
    fun playerOnPause()
}