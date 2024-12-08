package com.example.playlistmaker.presentation.ui.player

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.constants.Constants
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_INTENT = "track_intent"

        //        const val STATE_DEFAULT = 0
//        const val STATE_PREPARED = 1
//        const val STATE_PLAYING = 2
//        const val STATE_PAUSED = 3
        const val TIME_UPDATE_DELAY = 400L
    }

    //    private var playerState = STATE_DEFAULT
//    private var mediaPlayer = MediaPlayer()
    private lateinit var tracksPlayer: Track
    private lateinit var binding: ActivityPlayerBinding

    //    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val handler = Handler(Looper.getMainLooper())
    private val playerInteractor = Creator.providePlayerInteractor()


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = Gson().fromJson(
            intent.getStringExtra(TRACK_INTENT),
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
//        mediaPlayer.setDataSource(url)
//        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            binding.ibPlay.isEnabled = true
//            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.ibPlay.setImageResource(R.drawable.ic_play_button)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.ibPlay.setImageResource(R.drawable.ic_pause_button)
        playerState = STATE_PLAYING
        musicTimer()

    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.ibPlay.setImageResource(R.drawable.ic_play_button)
        playerState = STATE_PAUSED

    }

    private fun playBackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun musicTimer() {
        handler.postDelayed(
            object : Runnable {
                override fun run() {
                    if (playerState == STATE_PLAYING) {
                        binding.timingTrack.text = formatTime(mediaPlayer.currentPosition)
                        handler.postDelayed(this, TIME_UPDATE_DELAY)
                    } else if (playerState == STATE_PREPARED) {
                        binding.timingTrack.text = formatTime(0)
                    }
                }

            }, TIME_UPDATE_DELAY
        )

    }

    private fun formatTime(time: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

}