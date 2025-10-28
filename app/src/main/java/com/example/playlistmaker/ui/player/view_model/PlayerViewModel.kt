package com.example.playlistmaker.ui.player.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorite.FavoriteInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.ui.mediatec.state.PlaylistState
import com.example.playlistmaker.ui.player.state.PlayerPlaylistState
import com.example.playlistmaker.ui.player.state.AddToPlaylistStatus
import com.example.playlistmaker.ui.player.state.PlayerScreenState
import com.example.playlistmaker.service.PlayerServiceApi
import com.example.playlistmaker.service.PlayerUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val historyInteractor: SearchHistoryInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var timerJob: Job? = null
    private var isPlaying = false

    private var trackInfoLiveData = MutableLiveData<Track?>()
    fun getTrackInfoLiveData(): LiveData<Track?> = trackInfoLiveData

    private val playbackTimeLiveData = MutableLiveData<String>()
    fun getPlaybackTimeLiveData(): LiveData<String> = playbackTimeLiveData

    private var playerStateLiveData = MutableLiveData(PlayerScreenState.DEFAULT_STATE)
    fun getPlayerStateLiveData(): LiveData<PlayerScreenState> = playerStateLiveData

    private var _playerPlaylistState = MutableLiveData<PlayerPlaylistState>()
    val playerPlaylistState: LiveData<PlayerPlaylistState> = _playerPlaylistState

    private val _addToPlaylistStatus = MutableLiveData<AddToPlaylistStatus>()
    val addToPlaylistStatus: LiveData<AddToPlaylistStatus> = _addToPlaylistStatus

    init {
        loadPlaylists()
    }


    private fun preparePlayer(track: Track) {
        isPlaying = false
        playerStateLiveData.postValue(PlayerScreenState.PREPARED_STATE)
        playerInteractor.preparePlayer(
            track.previewUrl,
            {
                playerStateLiveData.postValue(PlayerScreenState.PREPARED_STATE)
            },
            {
                playerStateLiveData.postValue(PlayerScreenState.PREPARED_STATE)
                playbackTimeLiveData.postValue(DEFAULT_TIME)
            })
    }

    private fun startPlayer() {
        isPlaying = true
        playerStateLiveData.postValue(PlayerScreenState.PLAYING_STATE)
        // Управление воспроизведением перенесено в сервис
        playerServiceApi?.play()
    }

    private fun pausePlayer() {
        isPlaying = false
        playerServiceApi?.pause()
        playerStateLiveData.postValue(PlayerScreenState.PAUSED_STATE)
    }

    fun playbackControl() {
        when (playerStateLiveData.value) {
            PlayerScreenState.PLAYING_STATE -> pausePlayer()
            PlayerScreenState.PAUSED_STATE, PlayerScreenState.PREPARED_STATE -> startPlayer()
            PlayerScreenState.DEFAULT_STATE -> {
                isPlaying = false
            }

            null -> {}
        }
    }

    fun loadTrackState() {
        viewModelScope.launch {
            val track = trackInfoLiveData.value
            if (track != null) {
                val isFavorite = favoriteInteractor.inFavorite(track.trackId)
                track.isFavorite = isFavorite
                trackInfoLiveData.postValue(track)
            }
        }
    }


    fun onActivityPause() {
        // Не ставим на паузу при сворачивании, чтобы продолжалось воспроизведение
    }

    private fun postCurrentTime() { /* таймер теперь не нужен, время приходит из сервиса */ }


    fun onFavoriteClicked() {
        viewModelScope.launch {
            val track = trackInfoLiveData.value
            if (track != null) {
                if (track.isFavorite) {
                    track.isFavorite = false
                    trackInfoLiveData.postValue(track)
                    viewModelScope.launch {
                        favoriteInteractor.removeTrackFromFavorite(track)
                    }
                } else {
                    track.isFavorite = true
                    trackInfoLiveData.postValue(track)
                    viewModelScope.launch {
                        favoriteInteractor.addTrackToFavorite(track)
                    }
                }
            }
        }
    }

    suspend fun checkIsFavorite(trackId: Int): Boolean {
        return favoriteInteractor.inFavorite(trackId)
    }

    fun updateTrackFavoriteStatus(trackId: Int, isFavorite: Boolean) {
        val track = trackInfoLiveData.value
        if (track != null && track.trackId == trackId) {
            track.isFavorite = isFavorite
            trackInfoLiveData.postValue(track)
        }
    }

    fun setTrack(track: Track) {
        trackInfoLiveData.value = track
        preparePlayer(track)
        loadTrackState()
    }

    fun stopAndRelease() { /* управление ресурсами в сервисе */ }

    // ===== Работа с сервисом плеера =====
    private var playerServiceApi: PlayerServiceApi? = null

    fun attachService(api: PlayerServiceApi) {
        playerServiceApi = api
        // Подпишемся на состояние плеера сервиса и транслируем в LiveData экрана
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            api.playerState.collect { state ->
                when (state) {
                    PlayerUiState.Default -> {
                        playerStateLiveData.postValue(PlayerScreenState.DEFAULT_STATE)
                        playbackTimeLiveData.postValue(DEFAULT_TIME)
                    }
                    PlayerUiState.Prepared -> {
                        playerStateLiveData.postValue(PlayerScreenState.PREPARED_STATE)
                        playbackTimeLiveData.postValue(DEFAULT_TIME)
                    }
                    PlayerUiState.Playing -> {
                        playerStateLiveData.postValue(PlayerScreenState.PLAYING_STATE)
                    }
                    PlayerUiState.Paused -> {
                        playerStateLiveData.postValue(PlayerScreenState.PAUSED_STATE)
                    }
                    is PlayerUiState.Time -> playbackTimeLiveData.postValue(state.formatted)
                }
            }
        }
    }

    fun detachService() {
        playerServiceApi = null
        timerJob?.cancel()
        timerJob = null
    }

    fun onAppBackground() {
        val api = playerServiceApi ?: return
        if (playerStateLiveData.value == PlayerScreenState.PLAYING_STATE) {
            api.startForeground()
        }
    }

    fun onAppForeground() {
        playerServiceApi?.stopForeground()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .catch { error ->
                    _playerPlaylistState.postValue(PlayerPlaylistState.Error(error.message.toString()))
                }
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        _playerPlaylistState.postValue(PlayerPlaylistState.Empty)
                    } else {
                        _playerPlaylistState.postValue(PlayerPlaylistState.Content(playlists))
                    }
                }
        }
    }

    fun addToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val track = trackInfoLiveData.value ?: return@launch
            if (playlist.trackIds.contains(track.trackId)) {
                _addToPlaylistStatus.postValue(AddToPlaylistStatus.ALREADY_IN_PLAYLIST)
                return@launch
            }
            try {
                playlistInteractor.addTrackToPlaylist(track, playlist)
                _addToPlaylistStatus.postValue(AddToPlaylistStatus.ADDED)
                loadPlaylists()
            } catch (e: Exception) {
                _addToPlaylistStatus.postValue(AddToPlaylistStatus.ERROR)
            }
        }
    }


    companion object {
        private const val TIMER_DELAY = 300L
        private const val DEFAULT_TIME = "00:00"
    }
}


