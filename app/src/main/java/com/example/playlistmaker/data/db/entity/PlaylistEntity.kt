package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val pathUrl: String?,
    @ColumnInfo(name = "list_tracks_id")
    val trackIds: String?,
    @ColumnInfo(name = "number_of_tracks")
    val numberOfTracks: Int,
)
