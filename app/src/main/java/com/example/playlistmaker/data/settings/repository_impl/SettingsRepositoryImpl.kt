package com.example.playlistmaker.data.settings.repository_impl

import android.app.Application
import android.content.Context
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.domain.settings.SettingsRepository

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    companion object {
        const val THEME_PREF_KEY = "theme_pref_key"
    }

    private val sharedPreferences =
        context.getSharedPreferences(THEME_PREF_KEY, Application.MODE_PRIVATE)

    override fun saveDarkThemeState(themeState: Boolean) {
        sharedPreferences.edit()
            .putBoolean(THEME_PREF_KEY, themeState)
            .apply()
    }

    override fun getDarkThemeState(): Boolean {
        return sharedPreferences.getBoolean(THEME_PREF_KEY, false)
    }

    override fun getThemeStateNow(): Boolean {
        return sharedPreferences.contains(THEME_PREF_KEY)
    }
}