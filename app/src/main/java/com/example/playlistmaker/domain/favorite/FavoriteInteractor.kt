package com.example.playlistmaker.domain.favorite

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    suspend fun addTrackToFavorite(track: Track)
    suspend fun removeTrackFromFavorite(track: Track)
    suspend fun getTracksFromFavorite(): Flow<List<Track>>
//    suspend fun inFavorite(trackId: Int): Boolean
}