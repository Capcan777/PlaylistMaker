package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {
    @Insert(entity = PlaylistTrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPlaylist(playlistTrackEntity: PlaylistTrackEntity)

    @Query("SELECT trackId FROM playlist_tracks_table WHERE playlistId = :playlistId")
    suspend fun getTrackIdsForPlaylist(playlistId: String): List<Int>
}
