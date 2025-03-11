package com.example.playlistmaker.ui.mediatec

import com.example.playlistmaker.domain.model.Track

sealed interface FavoriteState {
    data class Content(val favoriteList: List<Track>) : FavoriteState
    data class Error(val message: String) : FavoriteState
}