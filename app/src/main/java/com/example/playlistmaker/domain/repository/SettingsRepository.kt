package com.example.playlistmaker.domain.repository

interface SettingsRepository {
    fun saveDarkThemeState(themeState: Boolean)
    fun getDarkThemeState(): Boolean
}