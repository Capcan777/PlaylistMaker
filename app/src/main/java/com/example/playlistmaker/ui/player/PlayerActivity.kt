package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.mediatec.NewPlaylistFragment
import com.example.playlistmaker.ui.player.state.PlayerScreenState
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()

    private var currentTrackId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        binding = ActivityPlayerBinding.inflate(inflater)
        setContentView(binding.root)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
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

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_bottom_sheet)


        // Получаем трек из Intent
        val trackJson = intent.getStringExtra(TRACK_INTENT)
        if (trackJson != null) {
            val track = Gson().fromJson(trackJson, Track::class.java)
            viewModel.setTrack(track)
        }


        binding.fallBack.setOnClickListener {
            finish()
        }

        viewModel.getTrackInfoLiveData().observe(this) { track ->
            track?.let {
                currentTrackId = it.trackId
                setTrackInfo(it)
                updateFavoriteButton(it.isFavorite)
            }
        }

        viewModel.loadTrackState()

        binding.favorite.setOnClickListener {
            viewModel.onFavoriteClicked()
        }
        binding.ibAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.ibPlay.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.newPlaylistButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.container_bottom_sheet, NewPlaylistFragment()) // или replace
                .addToBackStack("add_new_playlist")
                .commit()
        }


        viewModel.getPlayerStateLiveData().observe(this) {
            binding.ibPlay.setImageResource(
                when (it) {
                    PlayerScreenState.PLAYING_STATE -> R.drawable.ic_pause_button
                    PlayerScreenState.PAUSED_STATE -> R.drawable.ic_play_button
                    PlayerScreenState.PREPARED_STATE -> R.drawable.ic_play_button
                    PlayerScreenState.DEFAULT_STATE -> R.drawable.ic_play_button
                }
            )
        }

        viewModel.getPlaybackTimeLiveData().observe(this) {
            renderTimer(it)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onActivityPause()
    }

    override fun onResume() {
        super.onResume()
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

    companion object {
        const val TRACK_INTENT = "track_intent"
    }
}
