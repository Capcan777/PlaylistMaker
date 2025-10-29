package com.example.playlistmaker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.playlistmaker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.media.MediaPlayer
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerService : Service(), PlayerServiceApi {

    private val binder = PlayerBinder()
//    private val notificationId = 2001
//    private val channelId = "player_channel"

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null

    private var trackName: String = ""
    private var artistName: String = ""
    private var previewUrl: String = ""

    private val mediaPlayer: MediaPlayer by lazy { MediaPlayer() }
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val _playerState = MutableStateFlow<PlayerUiState>(PlayerUiState.Default)
    override val playerState: StateFlow<PlayerUiState> = _playerState

    inner class PlayerBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

    override fun onCreate() {
        super.onCreate()
        ensureChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        // Забираем данные трека при привязке
        intent?.let {
            trackName = it.getStringExtra(EXTRA_TRACK_NAME) ?: ""
            artistName = it.getStringExtra(EXTRA_ARTIST_NAME) ?: ""
            previewUrl = it.getStringExtra(EXTRA_PREVIEW_URL) ?: ""
        }
        // Не переинициализируем, если уже идёт воспроизведение
        if (!mediaPlayer.isPlaying && previewUrl.isNotEmpty() && previewUrl != "No previewUrl") {
            // Если не играем и не подготовлено — подготовим
            preparePlayer()
        }
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        // Освободим ресурсы при отвязке (остановка по условию закрытия экрана/приложения)
        stopPlaybackAndRelease()
        return super.onUnbind(intent)
    }

    private fun preparePlayer() {
        _playerState.value = PlayerUiState.Prepared
        if (previewUrl.isNotEmpty() && previewUrl != "No previewUrl") {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.setOnPreparedListener {
                _playerState.value = PlayerUiState.Prepared
            }
            mediaPlayer.setOnCompletionListener {
                _playerState.value = PlayerUiState.Prepared
                stopForegroundInternal(true)
            }
            mediaPlayer.prepareAsync()
        }
    }

    override fun setTrackInfo(trackName: String, artistName: String, previewUrl: String) {
        val isSame = this.trackName == trackName && this.artistName == artistName && this.previewUrl == previewUrl
        if (isSame) return

        val wasPlaying = mediaPlayer.isPlaying
        this.trackName = trackName
        this.artistName = artistName
        this.previewUrl = previewUrl

        if (!wasPlaying) {
            preparePlayer()
        } else {
            // Меняем трек во время проигрывания: останавливаем и готовим новый
            try { mediaPlayer.pause() } catch (_: Exception) {}
            stopTimer()
            preparePlayer()
        }
    }

    override fun play() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            _playerState.value = PlayerUiState.Playing
            startTimer()
        }
    }

    override fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            _playerState.value = PlayerUiState.Paused
            stopTimer()
        }
    }

    private fun startTimer() {
        stopTimer()
        timerJob = serviceScope.launch {
            while (mediaPlayer.isPlaying) {
                _playerState.value = PlayerUiState.Time(dateFormat.format(mediaPlayer.currentPosition))
                delay(TIME_DELAY)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun stopPlaybackAndRelease() {
        try {
            if (mediaPlayer.isPlaying) mediaPlayer.pause()
        } catch (_: Exception) {}
        stopTimer()
        stopForegroundInternal(true)
        mediaPlayer.reset()
        _playerState.value = PlayerUiState.Default
    }

    override fun startForeground() {
        val notification = buildNotification()
        if (notification != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        }
    }

    override fun stopForeground() {
        stopForegroundInternal(true) // Убираем уведомление полностью
    }

    private fun stopForegroundInternal(remove: Boolean) {
        try {
            if (remove) stopForeground(STOP_FOREGROUND_REMOVE) else stopForeground(STOP_FOREGROUND_DETACH)
            getSystemService(NotificationManager::class.java)?.cancel(NOTIFICATION_ID)
        } catch (_: Exception) {}
    }

    private fun buildNotification(): Notification? {
        // Показываем уведомление если плеер играет ИЛИ если состояние Playing
        val isCurrentlyPlaying = mediaPlayer.isPlaying || _playerState.value is PlayerUiState.Playing
        if (!isCurrentlyPlaying) return null

        val text = "$artistName - $trackName" // формат "Исполнитель - Название трека"
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pause_button)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(text)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val EXTRA_TRACK_NAME = "extra_track_name"
        const val EXTRA_ARTIST_NAME = "extra_artist_name"
        const val EXTRA_PREVIEW_URL = "extra_preview_url"
        const val NOTIFICATION_ID = 2001
        const val CHANNEL_ID = "player_channel"
        const val TIME_DELAY = 300L

    }
}


