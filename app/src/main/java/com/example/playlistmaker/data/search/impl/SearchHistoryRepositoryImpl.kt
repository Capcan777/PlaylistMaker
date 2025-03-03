package com.example.playlistmaker.data.search.impl

import android.content.SharedPreferences
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {
//
//    private val sharedPreferences =
//        context.getSharedPreferences(PREF_KEY_HISTORY, Application.MODE_PRIVATE)
//    private val gson = Gson()

    override fun addTrackToHistory(track: Track, historyList: ArrayList<Track>): ArrayList<Track> {
        historyList.removeAll {
            it.trackId == track.trackId
        }
        historyList.add(0, track)

        if (historyList.size > 10) {
            historyList.removeAt(10)
        }
        saveTrackToHistory(historyList)
        return historyList
    }

    override fun saveTrackToHistory(tracks: ArrayList<Track>) {
        val jsonString = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(PREF_KEY_HISTORY, jsonString)
            .apply()
    }

    override fun clearHistoryPref() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    override fun readTracksFromHistory(): ArrayList<Track> {
        val searchHistoryString = sharedPreferences.getString(PREF_KEY_HISTORY, null)
        val itemType = object : TypeToken<ArrayList<Track>>() {}.type
        return if (!searchHistoryString.isNullOrEmpty()) {
            gson.fromJson<ArrayList<Track>>(searchHistoryString, itemType)
        } else {
            arrayListOf<Track>()
        }
    }

    companion object {
        const val PREF_KEY_HISTORY = "search_history"
    }
}
