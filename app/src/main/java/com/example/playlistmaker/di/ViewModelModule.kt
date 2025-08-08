package com.example.playlistmaker.di

import android.app.Application
import com.example.playlistmaker.presentation.ui.search.TracksSearchViewModel
import com.example.playlistmaker.ui.mediatec.view_model.FavoriteViewModel
import com.example.playlistmaker.ui.mediatec.view_model.NewPlaylistViewModel
import com.example.playlistmaker.ui.mediatec.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        TracksSearchViewModel(get(), get())
    }
    viewModel {
        PlayerViewModel(get(), get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoriteViewModel(get())
    }

    viewModel {
        PlaylistViewModel()
    }

    viewModel {
        NewPlaylistViewModel(get(), get())
    }
}