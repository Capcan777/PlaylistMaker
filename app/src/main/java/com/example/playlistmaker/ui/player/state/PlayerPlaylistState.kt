package com.example.playlistmaker.ui.player.state

import com.example.playlistmaker.domain.model.Playlist

sealed interface PlayerPlaylistState {
    data class Content(val playlists: List<Playlist>): PlayerPlaylistState
    data class Error(val message: String): PlayerPlaylistState
    object Empty: PlayerPlaylistState
}