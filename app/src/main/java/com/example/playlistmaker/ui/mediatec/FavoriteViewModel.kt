package com.example.playlistmaker.ui.mediatec

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteViewModel() : ViewModel() {

    private val favoriteLiveData: MutableLiveData<Any>? = null
    fun getFavorites(): LiveData<Any>? = favoriteLiveData

}