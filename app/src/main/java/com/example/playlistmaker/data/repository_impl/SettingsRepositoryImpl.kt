package com.example.playlistmaker.data.repository_impl

import android.app.Application
import android.content.Context
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(context: Context) : SettingsRepository {
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

    companion object {
        const val THEME_PREF_KEY = "theme_pref_key"
    }
}