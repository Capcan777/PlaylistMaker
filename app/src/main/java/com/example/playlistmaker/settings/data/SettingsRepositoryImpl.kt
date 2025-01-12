package com.example.playlistmaker.settings.data

import android.app.Application
import android.content.Context
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val sharedPreferences =
        context.getSharedPreferences(Constants.THEME_PREF_KEY, Application.MODE_PRIVATE)

    override fun saveDarkThemeState(themeState: Boolean) {
        sharedPreferences.edit()
            .putBoolean(Constants.THEME_PREF_KEY, themeState)
            .apply()
    }

    override fun getDarkThemeState(): Boolean {
        return sharedPreferences.getBoolean(Constants.THEME_PREF_KEY, false)
    }
}