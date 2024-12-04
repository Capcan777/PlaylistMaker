package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun onSuccess(foundTracks: ArrayList<Track>)
        fun onNoResult()
        fun onNetworkError()

    }
}