package com.example.playlistmaker.ui.mediatec.state

import com.example.playlistmaker.domain.model.Track

sealed interface PlaylistState {
    data class Content(val playlist: List<Track>) : PlaylistState
    data class Error(val message: String) : PlaylistState
}