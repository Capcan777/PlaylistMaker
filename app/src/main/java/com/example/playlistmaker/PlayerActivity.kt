package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val track = Gson().fromJson(intent.getStringExtra("track"), Track::class.java)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            fallBack.setOnClickListener() { finish() }
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            countOfTrack.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
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
    }
}