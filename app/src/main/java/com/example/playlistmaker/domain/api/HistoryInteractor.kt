package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface HistoryInteractor {
    fun trackAddToHistory(tracks: ArrayList<Track>, track: Track)
    fun getHistory()
    fun clearHistory()
}