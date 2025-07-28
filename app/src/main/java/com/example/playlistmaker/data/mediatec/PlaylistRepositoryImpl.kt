package com.example.playlistmaker.data.mediatec

import com.example.playlistmaker.converters.PlaylistDbConvertor
import com.example.playlistmaker.data.db.PlaylistDataBase
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.repository.PlaylistRepository

class PlaylistRepositoryImpl(
    private val dataBase: PlaylistDataBase,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        require(playlist.title.isNotBlank())
        val playlistEntity = playlistDbConvertor.map(playlist)
        require(playlistEntity.id > 0)
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
}