package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    suspend fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>
}