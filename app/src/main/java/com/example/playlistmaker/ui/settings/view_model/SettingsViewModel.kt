package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator

class SettingsViewModel() : ViewModel() {

    private val sharingInteractor = Creator.provideSharingInteractor()
    private val settingsInteractor = Creator.provideSettingsInteractor()

    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme

    init {
        _isDarkTheme.value = settingsInteractor.getDarkThemeState()
    }

    fun onThemeSwitched(isChecked: Boolean) {
        settingsInteractor.saveDarkThemeState(isChecked)
        _isDarkTheme.value = isChecked
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