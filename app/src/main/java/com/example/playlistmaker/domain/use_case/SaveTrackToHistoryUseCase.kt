package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SaveTrackToHistoryUseCase(private val repository: SearchHistoryRepository) {
    fun execute(tracks: ArrayList<Track>, track: Track) {
        repository.saveTrackToHistory(tracks, track)
    }
}