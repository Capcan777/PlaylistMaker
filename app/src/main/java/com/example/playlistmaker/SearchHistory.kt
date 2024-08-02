package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.constants.Constants


class SearchHistory(private val sharedPreferences: SharedPreferences) {
    val historyTrackList = mutableListOf<Track>()

    init {
        sharedPreferences.getString(Constants.PREF_KEY_HISTORY, "")

    }


    fun readTracks() { //чтение

    }

    fun saveTracks() { //сохранение
        sharedPreferences.edit()
            .putString(Constants.PREF_KEY_HISTORY, historyTrackList.to)
    }

    fun clearTracks() { //очистка истории
        historyTrackList.clear()
        sharedPreferences.edit()
            .remove(Constants.PREF_KEY_HISTORY)
            .apply()

    }
}