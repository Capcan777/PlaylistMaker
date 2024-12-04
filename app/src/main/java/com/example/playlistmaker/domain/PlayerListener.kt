package com.example.playlistmaker.domain

interface PlayerListener {
    fun onPlayerStart()
    fun onPlayerStop()
    fun onPlayerPause()
}