package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Playlist

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
}