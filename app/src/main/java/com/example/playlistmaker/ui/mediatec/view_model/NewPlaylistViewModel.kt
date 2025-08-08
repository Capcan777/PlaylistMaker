package com.example.playlistmaker.ui.mediatec.view_model

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class NewPlaylistViewModel(private val interactor: PlaylistInteractor, application: Application) :
    AndroidViewModel(application) {

    private var imagePath: Uri? = null
    private var playlistTitle: String = ""
    private var playlistDescription: String = ""
    private var editingPlaylistId: Int? = null
    private var currentTrackIds: String? = null
    private var currentTrackAmount: Int = 0


    private val _playlistLiveData = MutableLiveData<Playlist>()
    val playlistLiveData: LiveData<Playlist> get() = _playlistLiveData

    private val _statusLiveData = MutableLiveData<String>()
    val statusLiveData: LiveData<String> get() = _statusLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    fun setPlaylist(playlist: Playlist) {
        _playlistLiveData.value = playlist
    }

    fun createPlaylist(title: String, desc: String, im: Uri?) {
        viewModelScope.launch {
            val playlist = Playlist(
                id = 0,
                title = playlistTitle,
                description = playlistDescription,
                pathUrl = imagePath as String?,
                trackIds = currentTrackIds,
                numberOfTracks = currentTrackAmount
            )
            interactor.createPlaylist(playlist)
        }
    }

//    fun createPlaylist() {
//        viewModelScope.launch {
//            try {
//                _statusLiveData.value = "Создание плейлиста ..."
//                val playlist =
//                    _playlistLiveData.value ?: throw Exception("Данные плейлиста не заданы")
//                if (playlist.title.isBlank()) {
//                    _errorLiveData.value = "Название плейлиста не может быть пустым"
//                    return@launch
//                }
//                interactor.createPlaylist(playlist)
//                _statusLiveData.value = "Плейлист успешно создан"
//            } catch (e: Exception) {
//                _errorLiveData.value = "Ошибка при создании плейлиста: ${e.message}"
//            }
//        }
//    }

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
            _errorLiveData.postValue("Ошибка при сохранении изображения: ${e.message}")
            null
        }
    }
}