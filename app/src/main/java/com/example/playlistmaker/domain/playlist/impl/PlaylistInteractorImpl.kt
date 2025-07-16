package com.example.playlistmaker.domain.playlist.impl

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun createPlaylist(playlist: Playlist) {
        playlistRepository.createPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistRepository.deletePlaylist(playlistId)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, trackId: Int, track: Track): Boolean {
        return playlistRepository.addTrackToPlaylist(playlistId, trackId, track)
    }

    override suspend fun getTracksListById(trackId: List<Int>): List<Track> {
        return playlistRepository.getTracksListById(trackId)
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist? {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override suspend fun updatePlaylistTracks(playlistId: Int, tracksIds: Int, amount: Int) {
        playlistRepository.updatePlaylistTracks(playlistId, tracksIds, amount)
    }
}