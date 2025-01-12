package com.example.playlistmaker.settings.ui.view_model

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.SettingsInteractor

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {


    private val intentState = MutableLiveData<Intent>()
    fun getIntentState(): LiveData<Intent> = intentState

//    companion object {
//        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                SettingsViewModel(
//                    this[APPLICATION_KEY] as Application
//                )
//            }
//        }
//    }

    fun saveDarkThemeState(checked: Boolean) {
        settingsInteractor.saveDarkThemeState(checked)
    }

    fun getDarkThemeState(): Boolean {
        return settingsInteractor.getDarkThemeState()
    }
}