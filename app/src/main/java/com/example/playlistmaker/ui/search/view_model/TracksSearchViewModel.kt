package com.example.playlistmaker.presentation.ui.search

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.impl.GetTrackUseCase
import com.example.playlistmaker.ui.search.state.SearchScreenState
import com.example.playlistmaker.ui.search.state.TrackUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TracksSearchViewModel(
    private val getTrackUseCase: GetTrackUseCase,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var latestSearchText: String? = null

    private var searchJob: Job? = null

    private val screenStateLiveData =
        MutableLiveData<SearchScreenState>().apply { value = SearchScreenState.Nothing }
    val screenState: LiveData<SearchScreenState> = screenStateLiveData

    private val _searchResults = MutableLiveData<ArrayList<Track>?>()
    val searchResults: LiveData<ArrayList<Track>?> = _searchResults

    private val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

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


    fun updateHistory() {
        val historyList = searchHistoryInteractor.readTracksFromHistory()
        if (historyList.isNotEmpty()) {
            screenStateLiveData.value =
                SearchScreenState.History(historyList.mapToUiModel())
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
        if (!response.isNullOrEmpty()) {
            screenStateLiveData.postValue(
                SearchScreenState.Tracks(response.mapToUiModel())
            )
            _searchResults.postValue(ArrayList(response))
        } else {
            screenStateLiveData.postValue(SearchScreenState.EmptyResult)
            _searchResults.postValue(arrayListOf())
        }
    }

    private fun List<Track>.mapToUiModel(): ArrayList<TrackUiModel> = ArrayList(
        map { track ->
            TrackUiModel(
                track = track,
                formattedTime = timeFormatter.format(track.trackTimeMillis)
            )
        }
    )

}