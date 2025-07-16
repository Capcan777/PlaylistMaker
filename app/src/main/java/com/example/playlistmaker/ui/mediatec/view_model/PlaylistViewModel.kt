package com.example.playlistmaker.ui.mediatec.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val application: Application,
    private val playlistRepository: PlaylistRepository,
) : AndroidViewModel(application) {

    private val playlistStateLiveData = MutableLiveData<Playlist>()

    fun getPlaylistLiveData(): LiveData<Playlist> = playlistStateLiveData

    private val tracksLiveData = MutableLiveData<List<Track>>()
    fun getTracksLiveData(): LiveData<List<Track>> = tracksLiveData

    var id = "0"

    fun setPlaylistData(id: String) {

        this.id = id
        viewModelScope.launch(Dispatchers.IO) {
            playlistStateLiveData.postValue(playlistRepository.getPlaylistById(id.toInt()))
        }
    }

    fun getTrackList() {
        viewModelScope.launch(Dispatchers.IO) {
            while (playlistStateLiveData.value == null) {
                setPlaylistData(id)
            }

            playlistRepository.getPlaylists(playlistStateLiveData.value!!).collect { trackList ->
                if (trackList.isNotEmpty()) tracksLiveData.postValue(trackList)
                else tracksLiveData.postValue(listOf())
            }
        }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            playlistRepository.deleteTrack(track, playlistStateLiveData.value?.id!!)
            getTrackList()
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.deletePlaylist(playlistStateLiveData.value!!)
        }
    }

//    fun getIntentMessage() : String {
//        val trackCount = "${playlistStateLiveData.value!!.tracksCount} ${application.getDeclination(playlistStateLiveData.value!!.tracksCount)}"
//        return playlistRepository.getShareMessage(tracksLiveData.value!!, playlistStateLiveData.value!!, trackCount)
//    }
}