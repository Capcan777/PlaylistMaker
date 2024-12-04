package com.example.playlistmaker.domain.use_cases_impl

import com.example.playlistmaker.domain.repository.TracksInteractor
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(private val tracksRepository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
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