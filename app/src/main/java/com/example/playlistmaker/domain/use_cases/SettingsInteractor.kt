package com.example.playlistmaker.domain.use_cases

interface SettingsInteractor {
    fun saveDarkThemeState(themeState: Boolean)
    fun getDarkThemeState(): Boolean
}