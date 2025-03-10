package com.example.playlistmaker.ui.mediatec

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistViewModel() : ViewModel() {

    private val playlistsLiveData: MutableLiveData<Any>? = null
    fun getPlaylists(): LiveData<Any>? = playlistsLiveData
}