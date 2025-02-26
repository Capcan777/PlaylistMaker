package com.example.playlistmaker.domain.settings

interface SettingsInteractor {
    fun saveDarkThemeState(themeState: Boolean)
    fun getDarkThemeState(): Boolean
    fun getThemeStateNow(): Boolean
}