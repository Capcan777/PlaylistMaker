package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

class SaveTrackToHistoryUseCase(private val repository: SearchHistoryRepository) {
    fun execute(tracks: ArrayList<Track>) {
        repository.saveTrackToHistory(tracks)
    }
}