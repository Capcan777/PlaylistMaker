package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTracksToFavorite(tracks: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun removeTrackFromFavorite(track: TrackEntity)

    @Query("SELECT * FROM track_table WHERE isFavorite = 1 ORDER BY lastAdded DESC") // сделать сортировку
    suspend fun getAllTracksFromFavorite(): List<TrackEntity>

    @Query("SELECT trackId FROM track_table WHERE isFavorite = 1")
    suspend fun getTracksIdList(): List<Int>

}