package com.example.playlistmaker.domain.use_case

interface SettingsInteractor {
    fun saveDarkThemeState(themeState: Boolean)
    fun getDarkThemeState(): Boolean
}