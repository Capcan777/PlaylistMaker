package com.example.playlistmaker.converters

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.model.Playlist

class PlaylistDbConvertor {
    // Конвертация из модели в сущность
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            title = playlist.title,
            description = playlist.description,
            pathUrl = playlist.pathUrl,
            numberOfTracks = playlist.numberOfTracks
        )
    }

    // Конвертация из сущности в модель
    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id = playlistEntity.id,
            title = playlistEntity.title,
            description = playlistEntity.description,
            pathUrl = playlistEntity.pathUrl,
            numberOfTracks = playlistEntity.numberOfTracks,
            trackIds = emptyList()
        )
    }
}