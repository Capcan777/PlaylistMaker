package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTrackUseCase(private val tracksRepository: TracksRepository) {

    suspend fun execute(expression: String): Flow<Pair<List<Track>?, String?>> {
        return tracksRepository.searchTracks(expression).map { result ->
            Pair(result.first, result.second)
        }
    }


}