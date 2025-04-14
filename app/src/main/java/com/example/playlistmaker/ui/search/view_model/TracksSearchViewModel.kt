package com.example.playlistmaker.presentation.ui.search

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.TrackConsumer
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.impl.GetTrackUseCase
import com.example.playlistmaker.ui.search.state.SearchScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TracksSearchViewModel(
    private val getTrackUseCase: GetTrackUseCase,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        //        private val SEARCH_REQUEST_TOKEN = Any()
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var latestSearchText: String? = null

    private var searchJob: Job? = null

    //    private val handler = Handler(Looper.getMainLooper())
    private val screenStateLiveData =
        MutableLiveData<SearchScreenState>().apply { value = SearchScreenState.Nothing }
    val screenState: LiveData<SearchScreenState> = screenStateLiveData

    private val _searchResults = MutableLiveData<ArrayList<Track>?>()
    val searchResults: LiveData<ArrayList<Track>?> = _searchResults


//    override fun onCleared() {
//        super.onCleared()
//        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
//    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTracks(changedText)
        }
//        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
//        val searchRunnable = Runnable { searchTracks(changedText) }
//        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
//        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }


    private fun searchTracks(newSearchText: String) {
        if (newSearchText.isEmpty()) return
        screenStateLiveData.value = SearchScreenState.Loading
        _searchResults.value = arrayListOf()

        viewModelScope.launch {
            getTrackUseCase.execute(newSearchText)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
        }
    }

//        getTrackUseCase.execute(newSearchText) {
//            if (newSearchText.isNotEmpty()) {
//                screenStateLiveData.postValue(SearchScreenState.Loading)
//                viewModelScope.launch {
//
//                }


//            override fun onSuccess(response: ArrayList<Track>) {
//                if (response.isNotEmpty()) {
//                    screenStateLiveData.postValue(SearchScreenState.Tracks(response))
//                    _searchResults.postValue(response)
//                } else {
//                    screenStateLiveData.postValue(SearchScreenState.EmptyResult)
//                }
//            }
//
//            override fun onNoResult() {
//                screenStateLiveData.postValue(SearchScreenState.EmptyResult)
//            }
//
//            override fun onNetworkError() {
//                screenStateLiveData.postValue(SearchScreenState.NetworkError)
//            }
//        })
//
//    }

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

    private fun processResult(response: List<Track>?, message: String?) {
        if (response.isNullOrEmpty()) {
            screenStateLiveData.postValue(SearchScreenState.Tracks(response as ArrayList<Track>))
            _searchResults.postValue(response)
        } else {
            screenStateLiveData.postValue(SearchScreenState.EmptyResult)
        }

    }

}