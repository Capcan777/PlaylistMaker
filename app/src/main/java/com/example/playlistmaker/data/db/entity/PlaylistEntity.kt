package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0,
    val playlistTitle: String,
    val playlistDescription: String,
    val urlImage: String?,
    val listTrackId: String? = null,
    val numberOfTracks: Int,
)