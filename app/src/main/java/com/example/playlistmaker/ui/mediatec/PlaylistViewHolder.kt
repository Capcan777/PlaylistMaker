package com.example.playlistmaker.ui.mediatec

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemViewBinding
import com.example.playlistmaker.domain.model.Playlist
import org.w3c.dom.Text

class PlaylistViewHolder(private val binding: PlaylistItemViewBinding): RecyclerView.ViewHolder(binding.root) {


    fun bind(playlist: Playlist) = with(binding) {
        tvTitle.text = playlist.title
        tvNumberTracks.text = playlist.numberTracks.toString()

        if (playlist.imageUrl.isBlank()) {
            Glide.with(itemView)
                .load(playlist.imageUrl)
                .placeholder(R.drawable.poster_placeholder)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(8.0F, itemView.context)))
                .into(playlistImage)
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}
