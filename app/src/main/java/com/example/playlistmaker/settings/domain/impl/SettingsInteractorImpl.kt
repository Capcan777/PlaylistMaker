package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.api.SettingsInteractor

class SettingsInteractorImpl(val settingsRepository: SettingsRepository) : SettingsInteractor {
    override fun saveDarkThemeState(themeState: Boolean) {
        settingsRepository.saveDarkThemeState(themeState)
    }

    override fun getDarkThemeState(): Boolean {
        return settingsRepository.getDarkThemeState()
    }
}