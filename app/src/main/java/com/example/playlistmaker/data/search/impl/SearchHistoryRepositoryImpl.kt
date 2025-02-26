package com.example.playlistmaker.data.search.impl

import android.app.Application
import android.content.Context
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(context: Context) : SearchHistoryRepository {

    private val sharedPreferences =
        context.getSharedPreferences(Constants.PREF_KEY_HISTORY, Application.MODE_PRIVATE)
    private val gson = Gson()

    override fun addTrackToHistory(track: Track, historyList: ArrayList<Track>): ArrayList<Track> {
        historyList.removeAll {
            it.trackId == track.trackId
        }
        historyList.add(0, track)
        // Установка ограничения списка треков в истории
        if (historyList.size > 10) {
            historyList.removeAt(10)
        }
        saveTrackToHistory(historyList)
        return historyList
    }

    override fun saveTrackToHistory(tracks: ArrayList<Track>) {
        val jsonString = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(Constants.PREF_KEY_HISTORY, jsonString)
            .apply()
    }

    override fun clearHistoryPref() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    override fun readTracksFromHistory(): ArrayList<Track> {
        val searchHistoryString = sharedPreferences.getString(Constants.PREF_KEY_HISTORY, null)
        val itemType = object : TypeToken<ArrayList<Track>>() {}.type
        return if (!searchHistoryString.isNullOrEmpty()) {
            gson.fromJson<ArrayList<Track>>(searchHistoryString, itemType)
        } else {
            arrayListOf<Track>()
        }
    }
}
