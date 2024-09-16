package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.constants.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchHistory(private val sharedPreferences: SharedPreferences) {
    var historyTrackList: ArrayList<Track> = arrayListOf()
    private val gson = Gson()
    val searchHistoryString = sharedPreferences.getString(Constants.PREF_KEY_HISTORY, null)

    // Сохранение списка треков в историю
    fun saveTrackToHistory(tracks: ArrayList<Track>) {
        val jsonString = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(Constants.PREF_KEY_HISTORY, jsonString)
            .apply()
    }

    // Очистка истории треков
    fun clearHistoryPref() {
        historyTrackList.clear()
        sharedPreferences.edit()
            .remove(Constants.PREF_KEY_HISTORY)
            .apply()
    }

    // Чтение списка треков из истории
    fun readTracksFromHistory(): ArrayList<Track> {

        return if (!searchHistoryString.isNullOrEmpty()) {
            createTrackListFromJson(searchHistoryString)
        } else {
            arrayListOf() // Возвращает пустой список, если истории нет
        }.also { historyTrackList = it }
    }

    // Преобразование JSON строки в список треков
    fun createTrackListFromJson(json: String): ArrayList<Track> {
        val itemType = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, itemType)
    }

}
