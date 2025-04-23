package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
data class TrackEntity(
    @PrimaryKey
    val trackId: Int,
    val artworkUrl100: String,
    val trackName: String,
    val artistName: String,
    var collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val trackTimeMillis: Long,
    val previewUrl: String,
    var isFavorite: Boolean = false,
    val lastAdded: Long = System.currentTimeMillis()
)
