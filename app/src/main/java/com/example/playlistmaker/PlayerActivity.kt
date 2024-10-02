package com.example.playlistmaker

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var playerState = Constants.STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
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

            fallBack.setOnClickListener() { finish() }
            ibPlay.setOnClickListener {
                playBackControl()
            }
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            timingTrack.text = formatTime(0)
            countOfTrack.text = formatTime(track.trackTimeMillis)
            dataTrack.text = track.releaseDate.slice(0..3)

            styleTrack.text = track.primaryGenreName
            countryTrack.text = track.country

            Glide.with(applicationContext)
                .load(track.getCoverArtwork())
                .centerCrop()
                .transform(
                    RoundedCorners(
                        resources.getDimensionPixelSize(R.dimen.radiusCorner_8)
                    )
                )
                .placeholder(R.drawable.poster_placeholder)
                .into(binding.ivPoster)

            if (track.collectionName.isNotEmpty())
                albumTrack.text = track.collectionName
            else {
                tvAlbumGroup.isVisible = false
            }


        }

        preparePlayer(url)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.ibPlay.isEnabled = true
            playerState = Constants.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.ibPlay.setImageResource(R.drawable.ic_play_button)
            playerState = Constants.STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.ibPlay.setImageResource(R.drawable.ic_pause_button)
        playerState = Constants.STATE_PLAYING
        musicTimer()

    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.ibPlay.setImageResource(R.drawable.ic_play_button)
        playerState = Constants.STATE_PAUSED

    }

    private fun playBackControl() {
        when (playerState) {
            Constants.STATE_PLAYING -> {
                pausePlayer()
            }

            Constants.STATE_PREPARED, Constants.STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun musicTimer() {
        handler.postDelayed(
            object : Runnable {
                override fun run() {
                    if (playerState == Constants.STATE_PLAYING) {
                        binding.timingTrack.text = formatTime(mediaPlayer.currentPosition)
                        handler.postDelayed(this, Constants.TIME_UPDATE_DELAY)
                    } else if (playerState == Constants.STATE_PREPARED) {
                        binding.timingTrack.text = formatTime(0)
                    }
                }

            }, Constants.TIME_UPDATE_DELAY
        )

    }

    private fun formatTime(time: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

}