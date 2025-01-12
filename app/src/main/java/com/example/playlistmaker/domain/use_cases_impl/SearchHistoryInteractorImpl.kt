package com.example.playlistmaker.domain.use_cases_impl

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.domain.use_cases.SearchHistoryInteractor

class SearchHistoryInteractorImpl(val searchHistoryRepository: SearchHistoryRepository) :
    SearchHistoryInteractor {

    override fun addTrackToHistory(track: Track, historyList: ArrayList<Track>): ArrayList<Track> {
        return searchHistoryRepository.addTrackToHistory(track, historyList)
    }

    override fun saveTrackToHistory(tracks: ArrayList<Track>) {
        searchHistoryRepository.saveTrackToHistory(tracks)
    }

    override fun clearHistoryPref() {
        searchHistoryRepository.clearHistoryPref()
    }

    override fun readTracksFromHistory(): ArrayList<Track> {
        return searchHistoryRepository.readTracksFromHistory()
    }
}