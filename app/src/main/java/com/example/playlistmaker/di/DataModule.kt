package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.network.ItunesApiService
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl.Companion.PREF_KEY_HISTORY
import com.example.playlistmaker.data.settings.repository_impl.SettingsRepositoryImpl.Companion.THEME_PREF_KEY
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    factory { Gson() }

    single<NetworkClient> { RetrofitNetworkClient(get(), androidContext()) }

    single {
        androidContext()
            .getSharedPreferences(PREF_KEY_HISTORY, Context.MODE_PRIVATE)
    }
    single(named("themePrefs")) {
        androidContext()
            .getSharedPreferences(THEME_PREF_KEY, Context.MODE_PRIVATE)
    }

    factory {
        MediaPlayer()
    }


}