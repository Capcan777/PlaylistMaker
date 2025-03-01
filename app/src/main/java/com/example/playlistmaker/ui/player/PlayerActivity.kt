package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private val viewModel: PlayerViewModel by viewModels {
        PlayerViewModel.provideFactory(
            Gson().fromJson(
                intent.getStringExtra(Constants.TRACK_INTENT),
                Track::class.java
            ), Creator.providePlayerInteractor()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = viewModel.getTrackInfo()

        binding.apply {
            fallBack.setOnClickListener { finish() }
            ibPlay.setOnClickListener { viewModel.playPause() }
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            timingTrack.text = viewModel.musicTimer.value ?: "00:00"
            countOfTrack.text =
                if (track.trackTimeMillis > 0) viewModel.trackDuration.value else "10:00"
            Log.d("PlayerViewModel", "Track timeMillis: ${viewModel.trackDuration.value}")
            dataTrack.text = track.releaseDate.slice(0..3)
            styleTrack.text = track.primaryGenreName
            countryTrack.text = track.country

            Glide.with(applicationContext)
                .load(track.getCoverArtwork())
                .centerCrop()
                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.radiusCorner_8)))
                .placeholder(R.drawable.poster_placeholder)
                .into(ivPoster)

            if (track.collectionName.isNotEmpty()) {
                albumTrack.text = track.collectionName
            } else {
                tvAlbumGroup.isVisible = false
            }
        }

        viewModel.playButtonRes.observe(this) { resId ->
            binding.ibPlay.setImageResource(resId)
        }
        viewModel.musicTimer.observe(this) { timer ->
            binding.timingTrack.text = timer
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }
}
