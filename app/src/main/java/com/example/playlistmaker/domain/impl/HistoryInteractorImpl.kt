package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class HistoryInteractorImpl(val searchHistoryRepository: SearchHistoryRepository) :
    HistoryInteractor {
    override fun trackAddToHistory(tracks: ArrayList<Track>, track: Track) {
        searchHistoryRepository.saveTrackToHistory(tracks, track)
    }

    override fun getHistory() {
        searchHistoryRepository.readTracksFromHistory()
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistoryPref()
    }

}