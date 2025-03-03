package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.player.state.PlaybackState
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        binding = ActivityPlayerBinding.inflate(inflater)
        setContentView(binding.root)

        binding.fallBack.setOnClickListener {
            finish()
        }

        viewModel.getTrackInfoLiveData().observe(this) {
            setTrackInfo(it)
        }

        binding.ibPlay.setOnClickListener {
            viewModel.playbackControl()
        }

        viewModel.getPlayerStateLiveData().observe(this) {
            binding.ibPlay.setImageResource(
                when (it) {
                    PlaybackState.PLAYING_STATE -> R.drawable.ic_pause_button
                    PlaybackState.PAUSED_STATE -> R.drawable.ic_play_button
                    PlaybackState.PREPARED_STATE -> R.drawable.ic_play_button
                    PlaybackState.DEFAULT_STATE -> R.drawable.ic_play_button
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
    }
}
