package com.example.playlistmaker.ui.mediatec.state

import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

sealed interface PlaylistState {
    data class Content(val playlist: Flow<List<Playlist>>) : PlaylistState
    object Empty : PlaylistState
}