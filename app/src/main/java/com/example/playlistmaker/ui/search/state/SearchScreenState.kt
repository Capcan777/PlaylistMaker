package com.example.playlistmaker.ui.search.state

import com.example.playlistmaker.domain.model.Track

sealed class SearchScreenState {
    object Loading: SearchScreenState()
    data class Tracks(val trackSearch: ArrayList<Track>?) : SearchScreenState()
    data class History(val historyList: ArrayList<Track>): SearchScreenState()
    object NetworkError: SearchScreenState()
    object EmptyResult: SearchScreenState()
    object Nothing: SearchScreenState()
}