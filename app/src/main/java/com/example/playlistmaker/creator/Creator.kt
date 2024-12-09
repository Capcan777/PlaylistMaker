package com.example.playlistmaker.creator

import android.annotation.SuppressLint
import android.content.Context
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository_impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository_impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository_impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository_impl.TracksRepositoryImpl
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.use_cases.PlayerInteractor
import com.example.playlistmaker.domain.use_cases.SearchHistoryInteractor
import com.example.playlistmaker.domain.use_cases.SettingsInteractor
import com.example.playlistmaker.domain.use_cases_impl.GetTrackUseCase
import com.example.playlistmaker.domain.use_cases_impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.use_cases_impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.use_cases_impl.SettingsInteractorImpl

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
}