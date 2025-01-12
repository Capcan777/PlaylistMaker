package com.example.playlistmaker.creator

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository_impl.TracksRepositoryImpl
import com.example.playlistmaker.player.api.PlayerRepository
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.player.api.PlayerInteractor
import com.example.playlistmaker.domain.use_cases.SearchHistoryInteractor
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.use_cases_impl.GetTrackUseCase
import com.example.playlistmaker.domain.use_cases_impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.use_cases_impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.presentation.ui.settings.SettingsController
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl

@SuppressLint("StaticFieldLeak")
object Creator {


    private var context: Context? = null

    fun initialize(context: Context) {
        if (Creator.context == null) {
            Creator.context = context.applicationContext
        }
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(provideNetworkClient())
    }

    fun provideTracksUseCase(): GetTrackUseCase {
        return GetTrackUseCase(getTracksRepository())
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(context as Context)
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    private fun provideNetworkClient(): NetworkClient {
        return RetrofitNetworkClient()
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(context as Context)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    fun provideSettingsController(activity: Activity): SettingsController {
        return SettingsController(activity)
    }
}