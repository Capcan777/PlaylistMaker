package com.example.playlistmaker.domain.settings

interface SettingsRepository {
    fun saveDarkThemeState(themeState: Boolean)
    fun getDarkThemeState(): Boolean
    fun getThemeStateNow(): Boolean
}