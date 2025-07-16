package com.example.playlistmaker.converters

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.model.Playlist

class PlaylistDbConvertor {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.id,
            playlistTitle = playlist.title,
            playlistDescription = playlist.description,
            urlImage = playlist.imageUrl,
            numberOfTracks = playlist.numberTracks,
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id = playlistEntity.playlistId,
            title = playlistEntity.playlistTitle,
            description = playlistEntity.playlistDescription,
            imageUrl = playlistEntity.urlImage!!,
            numberTracks = playlistEntity.numberOfTracks
        )
    }
}