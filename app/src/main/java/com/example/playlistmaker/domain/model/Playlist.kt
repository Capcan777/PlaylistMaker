package com.example.playlistmaker.domain.model

data class Playlist(
    val id: Int,
    val title: String,
    val description: String,
    val pathUrl: String?,
    val trackIds: String?,
    val numberOfTracks: Int
)
