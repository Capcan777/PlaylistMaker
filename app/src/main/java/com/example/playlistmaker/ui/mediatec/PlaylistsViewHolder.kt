package com.example.playlistmaker.ui.mediatec

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.domain.model.Playlist
import java.io.File

class PlaylistsViewHolder(private val binding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {


    fun bind(playlist: Playlist) = with(binding) {
        tvTitle.text = playlist.title
        val context = itemView.context
        val tracksCount = playlist.numberOfTracks
        try {
            val tracksString = context.resources.getQuantityString(
                R.plurals.numberOfTracks,
                tracksCount,
                tracksCount
            )
            tvNumberTracks.text = tracksString
        } catch (e: Resources.NotFoundException) {
            tvNumberTracks.text = "$tracksCount треков"
        }


        val source =
            playlist.pathUrl?.let { path -> if (path.startsWith("/")) File(path) else path }
        Glide.with(itemView)
            .load(source)
            .placeholder(R.drawable.poster_placeholder)
            .error(R.drawable.poster_placeholder)
            .transform(CenterCrop(), RoundedCorners(dpToPx(8.0F, context)))
            .into(playlistImage)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}
