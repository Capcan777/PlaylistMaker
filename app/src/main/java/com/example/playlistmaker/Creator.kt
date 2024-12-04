package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import com.example.playlistmaker.data.repository_impl.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository_impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository_impl.SettingsRepositoryImpl
import com.example.playlistmaker.domain.repository.HistoryInteractor
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.TracksInteractor
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.use_cases_impl.HistoryInteractorImpl
import com.example.playlistmaker.domain.use_cases_impl.TracksInteractorImpl
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.use_case.PlayerInteractor
import com.example.playlistmaker.domain.use_case.SettingsInteractor
import com.example.playlistmaker.domain.use_cases_impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.use_cases_impl.SettingsInteractorImpl

@SuppressLint("StaticFieldLeak")
object Creator {

    private var contex: Context? = null

    fun init(context: Context) {
        if (this.contex == null) {
            this.contex = context.applicationContext
        }
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(context)
    }

    fun provideHistoryInteractor(context: Context): HistoryInteractor {
        return HistoryInteractorImpl(provideSearchHistoryRepository(context))
    }

    private fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(contex as Context)
    }

    fun privideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideSettingsRepository())
    }

    fun providePlayerInteractor(context: Context): PlayerInteractor {
        return PlayerInteractorImpl(getTracksRepository())
    }


}