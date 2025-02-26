package com.example.playlistmaker.creator

import android.annotation.SuppressLint
import android.content.Context
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.player.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.settings.repository_impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository_impl.TracksRepositoryImpl
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.repository_impl.SharingRepositoryImpl
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.search.impl.GetTrackUseCase
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.SharingRepository
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl

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
    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(), getSharringRepository())
    }
    private fun getSharringRepository(): SharingRepository {
        return SharingRepositoryImpl(context as Context)
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigator(context as Context)
    }
}