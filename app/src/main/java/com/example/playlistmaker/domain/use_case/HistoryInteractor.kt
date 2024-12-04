package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.models.Track

interface HistoryInteractor {
    fun trackAddToHistory(tracks: ArrayList<Track>, track: Track)
    fun getHistory()
    fun clearHistory()
}