package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.SearchHistoryInteractor

class SearchHistoryInteractorImpl(private val searchHistoryRepository: SearchHistoryRepository) :
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