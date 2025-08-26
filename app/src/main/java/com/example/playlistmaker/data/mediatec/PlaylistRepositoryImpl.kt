package com.example.playlistmaker.data.mediatec

import com.example.playlistmaker.converters.PlaylistDbConvertor
import com.example.playlistmaker.data.db.PlaylistDataBase
import com.example.playlistmaker.data.db.PlaylistTrackDataBase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val dataBase: PlaylistDataBase,
    private val playlistTrackDataBase: PlaylistTrackDataBase,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        require(playlist.title.isNotBlank())
        val playlistEntity = playlistDbConvertor.map(playlist)
        dataBase.playlistDao().createPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        dataBase.playlistDao().deletePlaylistEntity(playlistEntity)

    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        dataBase.playlistDao().updatePlaylist(playlistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = dataBase.playlistDao().getPlaylists()
        val models = convertFromPlaylistEntity(playlists).map { playlist ->
            val trackIds = playlistTrackDataBase
                .playlistTrackDao()
                .getTrackIdsForPlaylist(playlist.id.toString())
            playlist.copy(trackIds = trackIds)
        }
        emit(models)
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        val playlistEntity = playlistDbConvertor.map(playlist)
        val playlistTrackEntity = PlaylistTrackEntity(
            trackId = track.trackId,
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
            playlistId = playlistEntity.id.toString()
        )
        playlistTrackDataBase.playlistTrackDao().addTrackToPlaylist(playlistTrackEntity)
        // Инкрементируем счётчик треков в плейлисте
        val updated = playlistEntity.copy(numberOfTracks = playlistEntity.numberOfTracks + 1)
        dataBase.playlistDao().updatePlaylist(updated)
    }


    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }
}