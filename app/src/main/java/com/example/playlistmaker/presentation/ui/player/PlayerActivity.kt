package com.example.playlistmaker.presentation.ui.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.PlayerListenerState
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity(), PlayerListenerState {

    private val playerInteractor = Creator.providePlayerInteractor()
    private lateinit var binding: ActivityPlayerBinding
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = Gson().fromJson(
            intent.getStringExtra(Constants.TRACK_INTENT),
            Track::class.java
        )
        val url = track.previewUrl

        binding.apply {
            fallBack.setOnClickListener { finish() }
            ibPlay.setOnClickListener { playerInteractor.playBackControl(this@PlayerActivity) }
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            timingTrack.text = playerInteractor.musicTimerFormat(0)
            countOfTrack.text = playerInteractor.musicTimerFormat(track.trackTimeMillis)
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

        playerInteractor.preparePlayer(url, this)
    }

    override fun onPause() {
        super.onPause()
        playerInteractor.pausePlayer(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.releaseMediaPlayer()
    }

    private fun updateMusicTimer() {
        handler.postDelayed(
            object : Runnable {
                override fun run() {
                    if (playerInteractor.musicTimerFormat(0) != null) {
                        binding.timingTrack.text = playerInteractor.musicTimerFormat(0)
                        handler.postDelayed(this, Constants.TIME_UPDATE_DELAY)
                    }
                }
            }, Constants.TIME_UPDATE_DELAY
        )
    }

    override fun playerOnStart() {
        binding.ibPlay.setImageResource(R.drawable.ic_pause_button)
        updateMusicTimer()
    }

    override fun playerOnStop() {
        binding.ibPlay.setImageResource(R.drawable.ic_play_button)
    }

    override fun playerOnPause() {
        binding.ibPlay.setImageResource(R.drawable.ic_play_button)
    }
}
