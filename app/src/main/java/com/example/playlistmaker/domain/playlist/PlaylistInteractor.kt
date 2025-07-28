package com.example.playlistmaker.domain.playlist

import com.example.playlistmaker.domain.model.Playlist

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
}