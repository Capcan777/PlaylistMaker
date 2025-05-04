package com.example.playlistmaker.data.search.impl

import android.annotation.SuppressLint
import android.util.Log
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.db.AppDataBase
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(private val networkClient: NetworkClient, private val dataBase: AppDataBase) : TracksRepository {

    @SuppressLint("SuspiciousIndentation")
    override suspend fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> =
        flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

            if (response.resultCode == -1) {
                emit(Pair(null, "Ошибка сети"))
                return@flow
            }

        if (response.resultCode == 200) {
            val listTracksIdInFavorites = dataBase.trackDao().getTracksIdList().toIntArray()
            val result = ((response as TrackSearchResponse).results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis.apply {
                        SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(it.trackTimeMillis)
                    },
                    it.artworkUrl100,
                    it.trackId,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl,
                    isFavorite = if(it.trackId in listTracksIdInFavorites) true else false
                )

            }) as ArrayList<Track>
            emit(Pair(result, null))
        } else {
            emit(Pair(null, null))
        }

    }
}
