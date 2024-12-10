package com.example.playlistmaker.domain.use_cases

import com.example.playlistmaker.domain.model.Track

interface SearchHistoryInteractor {
    fun addTrackToHistory(track: Track, historyList: ArrayList<Track>): ArrayList<Track>
    fun saveTrackToHistory(tracks: ArrayList<Track>)
    fun clearHistoryPref()
    fun readTracksFromHistory(): ArrayList<Track>
}