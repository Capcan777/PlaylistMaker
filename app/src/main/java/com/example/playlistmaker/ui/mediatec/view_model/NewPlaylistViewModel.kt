package com.example.playlistmaker.ui.mediatec.view_model

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class NewPlaylistViewModel(private val interactor: PlaylistInteractor, application: Application) :
    AndroidViewModel(application) {


    var playlistImage: String? = null

    fun createPlaylist(playlistTitle: String, playlistDescription: String, imagePath: Uri?) {
        viewModelScope.launch {
            val playlist = Playlist(
                id = 0,
                title = playlistTitle,
                description = playlistDescription,
                pathUrl = imagePath.toString(),
                numberOfTracks = 0
            )
            interactor.createPlaylist(playlist)
        }
    }


    fun savePictureToStorage(uri: Uri): String? {
        val filePath = File(
            getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlistCover"
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "pic_${System.currentTimeMillis()}.jpg")
        return try {
            getApplication<Application>().contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    BitmapFactory.decodeStream(inputStream)?.compress(
                        Bitmap.CompressFormat.JPEG,
                        30,
                        outputStream
                    )
                }
            }
            file.path
        } catch (e: IOException) {
            null
        }
    }
}