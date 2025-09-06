package com.example.playlistmaker.domain.sharing

interface SharingInteractor {
    fun shareLink()
    fun openTerms()
    fun openSupport()
    suspend fun sendPlaylist(playlistId: Int)
}
