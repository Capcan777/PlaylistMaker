package com.example.playlistmaker.ui.search.state

import com.example.playlistmaker.domain.model.Track

data class TrackUiModel(
    val track: Track,
    val formattedTime: String
)

sealed class SearchScreenState {
    object Loading : SearchScreenState()
    data class Tracks(val trackSearch: ArrayList<TrackUiModel>) : SearchScreenState()
    data class History(val historyList: ArrayList<TrackUiModel>) : SearchScreenState()
    object NetworkError : SearchScreenState()
    object EmptyResult : SearchScreenState()
    object Nothing : SearchScreenState()
}