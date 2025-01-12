package com.example.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun saveDarkThemeState(themeState: Boolean)
    fun getDarkThemeState(): Boolean
}