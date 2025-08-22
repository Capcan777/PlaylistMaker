package com.example.playlistmaker.ui.mediatec.state

import com.example.playlistmaker.domain.model.Playlist

sealed interface PlaylistState {
    data class Content(val playlist: List<Playlist>) : PlaylistState
    data class Error(val error: String) : PlaylistState
    object Empty : PlaylistState
}