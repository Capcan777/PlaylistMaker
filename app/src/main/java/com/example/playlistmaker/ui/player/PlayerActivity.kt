package com.example.playlistmaker.ui.player

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.mediatec.NewPlaylistFragment
import com.example.playlistmaker.ui.player.state.PlayerPlaylistState
import com.example.playlistmaker.ui.player.state.AddToPlaylistStatus
import com.example.playlistmaker.ui.player.state.PlayerScreenState
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.root.RootActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.example.playlistmaker.ui.custom.PlaybackButtonView
import com.example.playlistmaker.ui.broadcast.ConnectionStatusBroadcastReceiver
import com.google.gson.Gson
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.playlistmaker.service.PlayerService
import com.example.playlistmaker.service.PlayerServiceApi
import com.example.playlistmaker.service.PlayerUiState
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()

    private val connectionStatusBroadcastReceiver = ConnectionStatusBroadcastReceiver()

    private lateinit var adapter: PlayerPlaylistAdapter

    private var currentTrackId: Int = -1
    private var lastSelectedPlaylistId: Int? = null
    private var lastSelectedPlaylistTitle: String? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var playerService: PlayerService? = null
    private var isBound: Boolean = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? PlayerService.PlayerBinder ?: return
            playerService = binder.getService()
            isBound = true
            observePlayerState()
            // Передаем API сервиса во ViewModel
            viewModel.attachService(playerService!!)
            // Гарантированно передаем актуальные данные трека при подключении
            viewModel.getTrackInfoLiveData().value?.let { t ->
                playerService?.setTrackInfo(t.trackName, t.artistName, t.previewUrl)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            playerService = null
            viewModel.detachService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        binding = ActivityPlayerBinding.inflate(inflater)
        setContentView(binding.root)

        adapter = PlayerPlaylistAdapter()
        adapter.onPlaylistClickListener =
            PlayerPlaylistAdapter.OnPlaylistClickListener { playlist ->
                clickOnPlaylist(playlist)
            }
        binding.rvBottomSheet.adapter = adapter
        binding.rvBottomSheet.layoutManager = LinearLayoutManager(this)

        viewModel.playerPlaylistState.observe(PlayerActivity@ this) { state ->
            when (state) {
                is PlayerPlaylistState.Content -> {
                    adapter.setPlaylists(state.playlists)
                    binding.rvBottomSheet.isVisible = true
                }

                PlayerPlaylistState.Empty -> {
                    binding.rvBottomSheet.isVisible = false
                }

                is PlayerPlaylistState.Error -> {
                    Toast.makeText(this,
                        getString(R.string.error_loading_playlists), Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.addToPlaylistStatus.observe(this) { status ->
            when (status) {
                AddToPlaylistStatus.ADDED -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    val title = lastSelectedPlaylistTitle ?: getString(R.string.playlist)
                    Toast.makeText(this,
                        getString(R.string.added_into_playlist, title), Toast.LENGTH_SHORT).show()
                    val pid = lastSelectedPlaylistId
                    val tid = currentTrackId
                    if (pid != null && tid != -1) {
                        adapter.incrementTracksCountFor(pid, tid)
                    }
                }

                AddToPlaylistStatus.ALREADY_IN_PLAYLIST -> {
                    val title = lastSelectedPlaylistTitle ?: getString(R.string.playlist)
                    Toast.makeText(
                        this,
                        getString(R.string.track_already_add_into_playlist, title),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                AddToPlaylistStatus.ERROR ->
                    Toast.makeText(this,
                        getString(R.string.error_to_add_in_playlist), Toast.LENGTH_SHORT).show()

                null -> {}
            }
        }

        val trackJson = intent.getStringExtra(TRACK_INTENT)
        if (trackJson != null) {
            val track = Gson().fromJson(trackJson, Track::class.java)
            viewModel.setTrack(track)
        }

        // Привязка к сервису выполняется в onStart()

        val bottomSheetContainer = binding.bottomSheetTracks
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.rvBottomSheet.isVisible = true
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }


            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset + 1f
            }
        })


        binding.fallBack.setOnClickListener {
            finish()
        }

        binding.ibAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        viewModel.getTrackInfoLiveData().observe(this) { track ->
            track?.let {
                currentTrackId = it.trackId
                setTrackInfo(it)
                updateFavoriteButton(it.isFavorite)
                // Если сервис уже привязан, передаем информацию сразу
                if (isBound) {
                    playerService?.setTrackInfo(it.trackName, it.artistName, it.previewUrl)
                }
            }
        }


        viewModel.loadTrackState()

        binding.favorite.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        (binding.ibPlay as PlaybackButtonView).onToggleListener = object : PlaybackButtonView.OnToggleListener {
            override fun onPlay() {
                viewModel.playbackControl()
            }

            override fun onPause() {
                viewModel.playbackControl()
            }
        }

        binding.newPlaylistButton.setOnClickListener {
            val intent = Intent(this, RootActivity::class.java).apply {
                putExtra("open_fragment", "new_playlist")
            }
            startActivity(intent)
        }

        // Управление кнопкой воспроизведения будет приходить из состояния сервиса

        viewModel.getPlaybackTimeLiveData().observe(this) {
            renderTimer(it)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadPlaylists()
        // Привязываем сервис только если еще не привязан
        if (!isBound) {
            val track = viewModel.getTrackInfoLiveData().value
            val intent = Intent(this, PlayerService::class.java).apply {
                putExtra(PlayerService.EXTRA_TRACK_NAME, track?.trackName ?: "")
                putExtra(PlayerService.EXTRA_ARTIST_NAME, track?.artistName ?: "")
                putExtra(PlayerService.EXTRA_PREVIEW_URL, track?.previewUrl ?: "")
            }
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onPause() {
        super.onPause()
        // Сообщаем ViewModel о переходе в фон
        if (!isFinishing) viewModel.onAppBackground()
        // Дополнительно сразу включаем foreground напрямую из сервиса,
        // чтобы избежать возможных гонок состояния
        // НО только если Activity НЕ видима (т.е. приложение свернуто)
        val svc = playerService
        if (!isFinishing && !hasWindowFocus() && svc != null) {
            val currentState = svc.playerState.value
            if (currentState is PlayerUiState.Playing) {
                ensurePostNotificationsPermission()
                svc.startForeground()
            }
        }
        unregisterReceiver(connectionStatusBroadcastReceiver)
    }

    private fun ensurePostNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ContextCompat.registerReceiver(this, connectionStatusBroadcastReceiver,
            IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"),
            ContextCompat.RECEIVER_NOT_EXPORTED)
        // Явно скрываем уведомление при возврате на экран
        viewModel.onAppForeground()
        playerService?.stopForeground()
        viewModel.loadTrackState()
        if (currentTrackId != -1) {
            lifecycleScope.launch {
                val isFavorite = viewModel.checkIsFavorite(currentTrackId)
                updateFavoriteButton(isFavorite)
                viewModel.updateTrackFavoriteStatus(currentTrackId, isFavorite)
            }
        } else {
            viewModel.loadTrackState()
        }
    }

    override fun onStop() {
        super.onStop()
        // НЕ отвязываем сервис при сворачивании - только при закрытии экрана
    }

    override fun onDestroy() {
        super.onDestroy()
        // Отвязываем сервис только при закрытии экрана/приложения
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    private fun observePlayerState() {
        val svc = playerService ?: return
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                svc.playerState.collect { state ->
                    when (state) {
                        is PlayerUiState.Playing -> (binding.ibPlay as PlaybackButtonView).isPlaying = true
                        is PlayerUiState.Paused, PlayerUiState.Prepared, PlayerUiState.Default -> (binding.ibPlay as PlaybackButtonView).isPlaying = false
                        is PlayerUiState.Time -> renderTimer(state.formatted)
                    }
                }
            }
        }
        (binding.ibPlay as PlaybackButtonView).onToggleListener = object : PlaybackButtonView.OnToggleListener {
            override fun onPlay() { svc.play() }
            override fun onPause() { svc.pause() }
        }
    }

    private fun renderTimer(currentTime: String) {
        binding.tvTrackTime.text = currentTime
    }

    private fun setTrackInfo(trackOnPlayer: Track) {
        Glide.with(this)
            .load(trackOnPlayer.getCoverArtwork())
            .placeholder(
                R.drawable.poster_placeholder
            ).fitCenter()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.radiusCorner_8)))
            .into(binding.ivPoster)
        with(binding) {
            tvTrackName.text = trackOnPlayer.trackName
            tvArtistName.text = trackOnPlayer.artistName
            countOfTrack.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackOnPlayer.trackTimeMillis)
            tvTrackTime.text = getString(R.string.timing_track)
            albumTrack.text = trackOnPlayer.collectionName
            dataTrack.text = trackOnPlayer.releaseDate.slice(0..3)
            styleTrack.text = trackOnPlayer.primaryGenreName
            countryTrack.text = trackOnPlayer.country
        }
        updateFavoriteButton(trackOnPlayer.isFavorite)
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        binding.favorite.setImageResource(
            if (isFavorite) R.drawable.ic_button_active_like
            else R.drawable.ic_button_like
        )
    }

    private fun clickOnPlaylist(playlist: Playlist) {
        lastSelectedPlaylistId = playlist.id
        lastSelectedPlaylistTitle = playlist.title
        lifecycleScope.launch { viewModel.addToPlaylist(playlist) }
    }


    companion object {
        const val TRACK_INTENT = "track_intent"
    }
}
