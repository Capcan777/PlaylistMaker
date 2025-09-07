package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun getPlaylistById(playlistId: Int): Playlist
    fun getTracksOfPlaylist(playlistId: String): Flow<List<Track>>
    fun getTracksByIds(trackIds: List<Int>): Flow<List<Track>>
    suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Int)
    suspend fun isTrackInAnyPlaylist(trackId: Int): Boolean
}