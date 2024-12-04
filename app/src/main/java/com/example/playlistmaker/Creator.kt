package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import com.example.playlistmaker.data.repository_impl.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository_impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository_impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository_impl.SettingsRepositoryImpl
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.use_case.HistoryInteractor
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.TracksUseCase
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.use_cases_impl.HistoryInteractorImpl
import com.example.playlistmaker.domain.use_cases_impl.TracksUseCaseImpl
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

    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksUseCase(): TracksUseCase {
        return TracksUseCaseImpl(getTracksRepository())
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

    fun providePlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(providePlayerRepository())
    }


}