package com.example.playlistmaker.ui.mediatec

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.domain.model.Playlist

class PlaylistsViewHolder(private val binding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {


    fun bind(playlist: Playlist) = with(binding) {
        tvTitle.text = playlist.title
        tvNumberTracks.text = playlist.numberOfTracks.toString()
        if (playlist.pathUrl != "") {
            Glide.with(itemView.context)
                .load(playlist.pathUrl)
                .placeholder(R.drawable.poster_placeholder)
                .transform(CenterCrop(), RoundedCorners(dpToPx(8.0F, itemView.context)))
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
