package com.example.playlistmaker.data.sharing.repository_impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.converters.PlaylistDbConvertor
import com.example.playlistmaker.data.db.PlaylistDataBase
import com.example.playlistmaker.data.db.PlaylistTrackDataBase
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.data.sharing.model.EmailData
import com.example.playlistmaker.data.sharing.model.TrackDataSharing
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.sharing.SharingRepository

class SharingRepositoryImpl(
    private val context: Context,
    private val dataBase: PlaylistTrackDataBase,
    private val playlistDataBase: PlaylistDataBase,
    private val playlistDbConvertor: PlaylistDbConvertor
) : SharingRepository {
    override fun getShareAppLink(): String {
        return context.getString(R.string.practicum_link)
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.legal_practicum_offer)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            recipient = context.getString(R.string.e_mail_developer),
            subject = context.getString(R.string.topic_of_email),
            body = context.getString(R.string.text_in_body_email)
        )
    }

    override suspend fun getPlaylistInfoForSend(
        playlistId: Int
    ): TrackDataSharing {

        val playlistTrackEntity =
            dataBase.playlistTrackDao().getTracksOfPlaylist(playlistId.toString())
        val playlist = playlistDataBase.playlistDao().getPlaylistById(playlistId)
        val listOfTrack = convertFromPlaylistTrackEntity(playlistTrackEntity)
        return TrackDataSharing(
            playlistTitle = playlist.title,
            playlistDescription = playlist.description,
            playlistTrackList = listOfTrack,
            trackAmount = playlist.numberOfTracks
        )
    }

    private fun convertFromPlaylistTrackEntity(playlists: List<PlaylistTrackEntity>): List<Track> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

}