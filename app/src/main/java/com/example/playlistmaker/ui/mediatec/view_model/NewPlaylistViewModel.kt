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

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor, application: Application): AndroidViewModel(application) {

    var playlistPicture: String? = null

    suspend fun getPlaylistData(playlistId: Int) : Playlist? {
        playlistPicture = playlistInteractor.getPlaylistById(playlistId)?.imageUrl
        return playlistInteractor.getPlaylistById(playlistId)
    }
//
    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch { playlistInteractor.createPlaylist(playlist) }
    }
//
//    fun createPlaylist(title: String, desc: String, picUri: Uri?) {
//        val picPath = if(picUri != null) savePictureToStorage(picUri) else null
//        addPlaylist(Playlist(0, title, desc, picPath, 0))
//    }
//
//    fun updatePlaylist(id: Int, title: String, desc: String, picUri: Uri?) {
//        val picPath = if(picUri != null) savePictureToStorage(picUri) else null
//        viewModelScope.launch {
//            val picture = picPath ?: playlistPicture
//            playlistRepo.updatePlaylist(id, title, desc, picture)
//        }
//    }

    fun savePictureToStorage(uri: Uri) : String {
        val filePath = File(getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlistCover")
        if (!filePath.exists()) {
            filePath.mkdir()
        }
        val file = File(filePath, "pic_${System.currentTimeMillis()}.jpg" )
        val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.path
    }
}