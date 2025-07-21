package com.example.playlistmaker.ui.mediatec.view_model

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistViewModel(application: Application) : AndroidViewModel(application) {


    fun savePictureToStorage(uri: Uri) : String {
        val filePath = File(
            getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlistCover"
        )
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