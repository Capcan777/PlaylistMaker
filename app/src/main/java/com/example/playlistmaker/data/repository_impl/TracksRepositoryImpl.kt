package com.example.playlistmaker.data.repository_impl

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    it.trackId,
                    it.previewUrl,
                    it.trackTimeMillis,
                    it.collectionName,
                    it.artworkUrl100,
                    it.country,
                    it.primaryGenreName,
                    it.releaseDate
                )
            }
        } else {
            return emptyList()
        }
    }

}