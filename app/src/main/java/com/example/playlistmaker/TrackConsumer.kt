package com.example.playlistmaker

import com.example.playlistmaker.domain.model.Track

interface TrackConsumer {
    fun onSuccess(response: ArrayList<Track>)
    fun onNoResult()
    fun onNetworkError()
}