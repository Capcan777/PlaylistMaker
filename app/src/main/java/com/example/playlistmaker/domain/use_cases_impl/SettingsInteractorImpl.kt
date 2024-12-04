package com.example.playlistmaker.domain.use_cases_impl

import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.use_case.SettingsInteractor

class SettingsInteractorImpl(val settingsRepository: SettingsRepository) : SettingsInteractor {
    override fun saveDarkThemeState(themeState: Boolean) {
        settingsRepository.saveDarkThemeState(themeState)
    }

    override fun getDarkThemeState(): Boolean {
        return settingsRepository.getDarkThemeState()
    }

}