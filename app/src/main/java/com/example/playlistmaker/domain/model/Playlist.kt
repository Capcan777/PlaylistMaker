package com.example.playlistmaker.domain.model

data class Playlist(val id: Int,
                    val title: String,
                    val description: String,
                    val imageUrl: String,
                    var numberTracks: Int = 0)
