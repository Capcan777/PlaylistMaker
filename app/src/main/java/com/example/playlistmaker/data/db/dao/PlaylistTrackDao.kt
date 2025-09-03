package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {
    @Insert(entity = PlaylistTrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPlaylist(playlistTrackEntity: PlaylistTrackEntity)

    @Query("SELECT trackId FROM playlist_tracks_table WHERE playlistId = :playlistId")
    suspend fun getTrackIdsForPlaylist(playlistId: String): List<Int>

    @Query("SELECT * FROM playlist_tracks_table WHERE playlistId = :playlistId")
    suspend fun getTracksOfPlaylist(playlistId: String): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_tracks_table WHERE trackId = :trackId AND playlistId = :playlistId")
    suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: String)

    @Update(entity = PlaylistTrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateTracksOfPlaylist(playlistTrackEntity: PlaylistTrackEntity)

    @Query("SELECT COUNT(*) FROM playlist_tracks_table WHERE trackId = :trackId")
    suspend fun getTrackUsageCount(trackId: Int): Int

}
