package com.example.playlistmaker.data.mediatec

import com.example.playlistmaker.converters.PlaylistDbConvertor
import com.example.playlistmaker.data.db.PlaylistDataBase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val dataBase: PlaylistDataBase,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        dataBase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        dataBase.playlistDao().deletePlaylist(playlistId)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlist = dataBase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        dataBase.playlistDao().updatePlaylist(playlistEntity)
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, trackId: Int, track: Track): Boolean {
        val playlist = dataBase.playlistDao().getPlaylistById(playlistId)
        if (playlist != null) {
            val playlistTrackEntity = PlaylistTrackEntity(
                trackId = trackId,
                artworkUrl100 = track.artworkUrl100,
                trackName = track.trackName,
                artistName = track.artistName,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                trackTimeMillis = track.trackTimeMillis,
                previewUrl = track.previewUrl,
                isFavorite = track.isFavorite,
            )
            dataBase.playlistTrackDao().insertTrackToPlaylist(playlistTrackEntity)
            return true
        }
        return false
    }

    override suspend fun getTracksListById(trackId: List<Int>): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist? {
        TODO("Not yet implemented")
    }

    override suspend fun updatePlaylistTracks(playlistId: Int, tracksIds: Int, amount: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int) {
        dataBase.playlistTrackDao().deleteTrackFromPlaylist(playlistId, trackId)
    }


    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
    return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
}




}