package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.TrackConsumer
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TracksRepository
import java.util.concurrent.Executors

class GetTrackUseCase(private val tracksRepository: TracksRepository) {
    private val executor = Executors.newSingleThreadExecutor()

    fun execute(expression: String, consumer: TrackConsumer) {
        executor.execute {
            try {
                val foundTracks = tracksRepository.searchTracks(expression) as ArrayList<Track>
                if (foundTracks.isNotEmpty()) {
                    consumer.onSuccess(foundTracks)
                } else {
                    consumer.onNoResult()
                }
            } catch (e: Exception) {
                consumer.onNetworkError()
            }
        }
    }


}