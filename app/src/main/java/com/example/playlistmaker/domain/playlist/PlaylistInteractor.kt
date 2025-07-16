package com.example.playlistmaker.domain.playlist

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistId: Int)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun addTrackToPlaylist(playlistId: Int, trackId: Int, track: Track) : Boolean
    suspend fun getTracksListById(trackId: List<Int>) : List<Track>
    suspend fun getPlaylistById(playlistId: Int) : Playlist?
    suspend fun updatePlaylistTracks(playlistId: Int, tracksIds: Int, amount: Int)
}