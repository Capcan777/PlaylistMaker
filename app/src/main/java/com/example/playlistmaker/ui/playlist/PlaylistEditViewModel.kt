package com.example.playlistmaker.ui.playlist

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.ui.mediatec.view_model.NewPlaylistViewModel
import kotlinx.coroutines.launch

class PlaylistEditViewModel(
    private val interactor: PlaylistInteractor,
    application: Application
) : NewPlaylistViewModel(interactor, application) {

    fun updatePlaylist(
        playlistId: Int,
        playlistTitle: String,
        playlistDescription: String,
        imagePath: Uri?
    ) {
        viewModelScope.launch {
            val updatedPlaylist = Playlist(
                id = playlistId,
                title = playlistTitle,
                description = playlistDescription,
                pathUrl = playlistImage ?: imagePath?.toString(),
                numberOfTracks = 0 // Количество треков не изменяется при редактировании
            )
            interactor.updatePlaylist(updatedPlaylist)
        }
    }
}