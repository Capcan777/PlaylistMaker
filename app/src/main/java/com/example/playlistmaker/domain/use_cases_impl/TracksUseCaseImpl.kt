package com.example.playlistmaker.domain.use_cases_impl

import com.example.playlistmaker.domain.repository.TracksUseCase
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class TracksUseCaseImpl(private val tracksRepository: TracksRepository) : TracksUseCase {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksUseCase.TracksConsumer) {
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
        // сделать проверку ответа
    }
}