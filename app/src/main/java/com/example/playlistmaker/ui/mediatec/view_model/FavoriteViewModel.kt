package com.example.playlistmaker.ui.mediatec.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorite.FavoriteInteractor
import com.example.playlistmaker.ui.mediatec.state.FavoriteState
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteInteractor: FavoriteInteractor) : ViewModel() {


    private val favoriteLiveData = MutableLiveData<FavoriteState>()
    fun getFavorites(): LiveData<FavoriteState> = favoriteLiveData

    init {
        loadFavoriteTracks()
    }

     fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoriteInteractor.getTracksFromFavorite().collect { list ->
                if (list.isNotEmpty()) {
                    favoriteLiveData.postValue(FavoriteState.Content(list))

                } else {
                    favoriteLiveData.postValue(FavoriteState.Empty)
                }
            }

        }
    }

}