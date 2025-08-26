package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity

@Database(version = 1, entities = [PlaylistTrackEntity::class])
abstract class PlaylistTrackDataBase : RoomDatabase() {
    abstract fun playlistTrackDao(): PlaylistTrackDao
}