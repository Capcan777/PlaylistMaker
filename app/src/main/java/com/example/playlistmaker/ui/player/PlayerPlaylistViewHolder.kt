package com.example.playlistmaker.ui.player

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.BottomSheetItemBinding
import com.example.playlistmaker.domain.model.Playlist

class PlayerPlaylistViewHolder(private val binding: BottomSheetItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) = with(binding) {
       tvPlaylistTitle.text = playlist.title
        tvTracksNumber.text = playlist.numberOfTracks.toString()
        if (playlist.pathUrl != "") {
            Glide.with(itemView.context)
                .load(playlist.pathUrl)
                .placeholder(R.drawable.poster_placeholder)
                .transform(CenterCrop(), RoundedCorners(dpToPx(8.0F, itemView.context)))
                .into(ivPlaylistImage)
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
