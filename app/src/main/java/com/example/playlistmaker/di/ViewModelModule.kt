package com.example.playlistmaker.di

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.presentation.ui.search.TracksSearchViewModel
import com.example.playlistmaker.ui.mediatec.view_model.FavoriteViewModel
import com.example.playlistmaker.ui.mediatec.view_model.NewPlaylistViewModel
import com.example.playlistmaker.ui.mediatec.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.playlist.PlaylistEditViewModel
import com.example.playlistmaker.ui.playlist.PlaylistViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        TracksSearchViewModel(get(), get())
    }
    viewModel {
        PlayerViewModel(get(), get(), get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoriteViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get(), get())
    }

    viewModel {
        PlaylistViewModel(get(), get())
    }

    viewModel {
        PlaylistEditViewModel(get(), get())
    }

}