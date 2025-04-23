package com.example.playlistmaker.data.mediatec

import com.example.playlistmaker.converters.TrackDbConvertor
import com.example.playlistmaker.data.db.AppDataBase
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(private val dataBase: AppDataBase, private val trackDbConvertor: TrackDbConvertor) :
    FavoriteTracksRepository {

    override suspend fun addTrackToFavorite(track: Track) {
        val trackEntity = convertToTrackEntity(track)
        dataBase.trackDao().addTracksToFavorite(trackEntity)
    }

    override suspend fun removeTrackFromFavorite(track: Track) {
        val trackEntity = convertToTrackEntity(track)
        dataBase.trackDao().removeTrackFromFavorite(trackEntity)
    }

    override suspend fun getTracksFromFavorite(): Flow<List<Track>> = flow {
        val tracks = dataBase.trackDao().getAllTracksFromFavorite()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun inFavorite(trackId: Int): Boolean {
        val favoriteTracks = dataBase.trackDao().getTracksIdList()
        return if (trackId in favoriteTracks) true else false
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertToTrackEntity(tracks: Track): TrackEntity {
        return trackDbConvertor.map(tracks)
    }

}