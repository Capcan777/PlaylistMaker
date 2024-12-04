package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.constants.Constants

class App : Application() {
    var darkTheme = false
//    lateinit var sharedPreferences: SharedPreferences

    companion object {}

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Creator.init(this.applicationContext)
//
//        sharedPreferences = getSharedPreferences(Constants.PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        darkTheme = Creator.privideSettingsInteractor().getDarkThemeState()
        applyTheme(darkTheme)

    }

//    fun switchTheme(darkThemeEnabled: Boolean) {
//        darkTheme = darkThemeEnabled
//        applyTheme(darkThemeEnabled)
//        sharedPreferences.edit()
//            .putBoolean(Constants.THEME_PREF_KEY, darkTheme)
//            .apply()
//    }

    fun applyTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}