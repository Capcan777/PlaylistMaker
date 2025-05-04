package com.example.playlistmaker.ui.mediatec.state

import com.example.playlistmaker.domain.model.Track

sealed interface FavoriteState {
    data class Content(val favoriteList: List<Track>) : FavoriteState
    object Empty : FavoriteState
}