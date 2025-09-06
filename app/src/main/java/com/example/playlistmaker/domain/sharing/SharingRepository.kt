package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.data.sharing.model.EmailData
import com.example.playlistmaker.data.sharing.model.TrackDataSharing
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track

interface SharingRepository {
    fun getShareAppLink(): String
    fun getTermsLink(): String
    fun getSupportEmailData(): EmailData
    suspend fun getPlaylistInfoForSend(playlistId: Int): TrackDataSharing
}