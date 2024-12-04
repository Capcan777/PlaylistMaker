package com.example.playlistmaker.domain.use_cases_impl

import com.example.playlistmaker.domain.use_case.HistoryInteractor
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
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