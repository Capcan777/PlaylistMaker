package com.example.playlistmaker.domain.favorite.impl

import com.example.playlistmaker.domain.favorite.FavoriteInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(private val favoriteTracksRepository: FavoriteTracksRepository) : FavoriteInteractor {

    override suspend fun addTrackToFavorite(track: Track) {
        favoriteTracksRepository.addTrackToFavorite(track)
    }

    override suspend fun removeTrackFromFavorite(track: Track) {
        favoriteTracksRepository.removeTrackFromFavorite(track)
    }

    override suspend fun getTracksFromFavorite(): Flow<List<Track>> {
        return favoriteTracksRepository.getTracksFromFavorite()
    }

//    override suspend fun inFavorite(trackId: Int): Boolean {
//        return favoriteTracksRepository.inFavorite(trackId)
//    }
}