package com.example.playlistmaker.data.repository_impl

import android.content.Context
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(context: Context) : SearchHistoryRepository {

    private var historyTrackList: ArrayList<Track> = arrayListOf()
    private val sharedPreferences =
        context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
    private val gson = Gson()

    override fun saveTrackToHistory(tracks: ArrayList<Track>, track: Track) {
        val jsonString = gson.toJson(tracks)
        if (track in tracks) {
            tracks.remove(track)
        } else if (tracks.size > 10) {
            tracks.removeAt(10)
        }

        tracks.add(0, track)
        sharedPreferences.edit()
            .putString(PREF_KEY_HISTORY, jsonString)
            .apply()
    }

    override fun clearHistoryPref() {
        historyTrackList.clear()
        sharedPreferences.edit()
            .remove(PREF_KEY_HISTORY)
            .apply()
    }

    override fun readTracksFromHistory(): ArrayList<Track> {
        val searchHistoryString = sharedPreferences.getString(PREF_KEY_HISTORY, null)
        val itemType = object : TypeToken<ArrayList<Track>>() {}.type
        return if (!searchHistoryString.isNullOrEmpty()) {
            gson.fromJson(searchHistoryString, itemType)

        } else {
            arrayListOf<Track>() // Возвращает пустой список, если истории нет
        }.also { historyTrackList = it }
    }


    companion object {
        const val PREF_KEY_HISTORY = "search_history"
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_pref"
    }

}