package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface SearchHistoryRepository {
    fun addTrackToHistory(track: Track, historyList: ArrayList<Track>): ArrayList<Track>
    fun saveTrackToHistory(tracks: ArrayList<Track>)
    fun clearHistoryPref()
    fun readTracksFromHistory(): ArrayList<Track>
}