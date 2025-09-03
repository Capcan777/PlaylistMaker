package com.example.playlistmaker.converters

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track

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

    //Конвертация из сущьности в модель
    fun map(playlistTrackEntity: PlaylistTrackEntity): Track {
        return Track(
            trackName = playlistTrackEntity.trackName,
            artistName = playlistTrackEntity.artistName,
            trackTimeMillis = playlistTrackEntity.trackTimeMillis,
            artworkUrl100 = playlistTrackEntity.artworkUrl100,
            trackId = playlistTrackEntity.trackId,
            collectionName = playlistTrackEntity.collectionName,
            releaseDate = playlistTrackEntity.releaseDate,
            primaryGenreName = playlistTrackEntity.primaryGenreName,
            country = playlistTrackEntity.country,
            previewUrl = playlistTrackEntity.previewUrl,
            isFavorite = playlistTrackEntity.isFavorite
        )
    }

    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
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
            isFavorite = track.isFavorite
        )
    }

}