package com.example.playlistmaker.domain.playlist.impl

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {
    override suspend fun createPlaylist(playlist: Playlist) {
        repository.createPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        return repository.getPlaylistById(playlistId)
    }

    override fun getTracksOfPlaylist(playlistId: String): Flow<List<Track>> {
        return repository.getTracksOfPlaylist(playlistId)
    }

    override fun getTracksByIds(trackIds: List<Int>): Flow<List<Track>> {
        return repository.getTracksByIds(trackIds)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist?) {
        repository.deleteTrackFromPlaylist(track, playlist)
    }

}