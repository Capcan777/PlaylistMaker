package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun saveTrackToHistory(tracks: ArrayList<Track>, track: Track)
    fun clearHistoryPref()
    fun readTracksFromHistory(): ArrayList<Track>
}