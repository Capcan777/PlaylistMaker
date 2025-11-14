package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(settingsInteractor.getDarkThemeState())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun onThemeSwitched(isChecked: Boolean) {
        viewModelScope.launch {
            settingsInteractor.saveDarkThemeState(isChecked)
            _isDarkTheme.value = isChecked
        }
    }

    fun shareLink() {
        sharingInteractor.shareLink()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }
}
