package com.example.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun saveDarkThemeState(themeState: Boolean)
    fun getDarkThemeState(): Boolean
}