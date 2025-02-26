package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.SettingsInteractor

class SettingsInteractorImpl(val settingsRepository: SettingsRepository) : SettingsInteractor {
    override fun saveDarkThemeState(themeState: Boolean) {
        settingsRepository.saveDarkThemeState(themeState)
    }

    override fun getDarkThemeState(): Boolean {
        return settingsRepository.getDarkThemeState()
    }

    override fun getThemeStateNow(): Boolean {
        return settingsRepository.getThemeStateNow()
    }
}