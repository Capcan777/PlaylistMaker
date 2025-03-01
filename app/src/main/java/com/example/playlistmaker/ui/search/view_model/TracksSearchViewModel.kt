package com.example.playlistmaker.presentation.ui.search

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.TrackConsumer
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.search.state.SearchScreenState

class TracksSearchViewModel() : ViewModel() {

    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val getTrackUseCase = Creator.provideTracksUseCase()
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()
    private var latestSearchText: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private val screenStateLiveData =
        MutableLiveData<SearchScreenState>().apply { value = SearchScreenState.Nothing }
    val screenState: LiveData<SearchScreenState> = screenStateLiveData

    private val _searchResults = MutableLiveData<ArrayList<Track>>()
    val searchResults: LiveData<ArrayList<Track>> = _searchResults


    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchTracks(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }


    private fun searchTracks(newSearchText: String) {
        if (newSearchText.isEmpty()) return
        screenStateLiveData.value = SearchScreenState.Loading
        _searchResults.value = arrayListOf()

        getTrackUseCase.execute(newSearchText, object : TrackConsumer {
            override fun onSuccess(response: ArrayList<Track>) {
                if (response.isNotEmpty()) {
                    screenStateLiveData.postValue(SearchScreenState.Tracks(response))
                    _searchResults.postValue(response)
                } else {
                    screenStateLiveData.postValue(SearchScreenState.EmptyResult)
                }
            }

            override fun onNoResult() {
                screenStateLiveData.postValue(SearchScreenState.EmptyResult)
            }

            override fun onNetworkError() {
                screenStateLiveData.postValue(SearchScreenState.NetworkError)
            }
        })

    }

    fun updateHistory() {
        val historyList = searchHistoryInteractor.readTracksFromHistory()
        if (historyList.isNotEmpty()) {
            screenStateLiveData.value = SearchScreenState.History(historyList)
        } else {
            screenStateLiveData.value = SearchScreenState.Nothing
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistoryPref()
        screenStateLiveData.value = SearchScreenState.Nothing
    }

    fun addTrackToHistory(track: Track) {
        val currentHistory = searchHistoryInteractor.readTracksFromHistory()
        searchHistoryInteractor.addTrackToHistory(track, currentHistory)
    }
}