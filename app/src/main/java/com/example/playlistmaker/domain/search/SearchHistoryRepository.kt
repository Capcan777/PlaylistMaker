package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.model.Track

interface SearchHistoryRepository {
    fun addTrackToHistory(track: Track, historyList: ArrayList<Track>): ArrayList<Track>
    fun saveTrackToHistory(tracks: ArrayList<Track>)
    fun clearHistoryPref()
    fun readTracksFromHistory(): ArrayList<Track>
}