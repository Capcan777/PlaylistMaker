package com.example.playlistmaker.di

import com.example.playlistmaker.data.player.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository_impl.TracksRepositoryImpl
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.settings.repository_impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.repository_impl.SharingRepositoryImpl
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.sharing.SharingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }
    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }
    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
    single<SharingRepository> {
        SharingRepositoryImpl(androidContext())
    }
    single<ExternalNavigator> {
        ExternalNavigator(androidContext())
    }
    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }
}