package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val itemImagePoster: ImageView = itemView.findViewById(R.id.ivPoster)
    private val itemTextTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val itemTextArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
    private val itemTextTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)

    fun bind(item: Track) {
        itemTextTrackName.text = item.trackName
        itemTextArtistName.text = item.artistName
        itemTextTrackTime.text = item.trackTime
        Glide.with(itemView)
            .load(item.artWorkUrl100)
            .placeholder(R.drawable.placeholder_24)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2.0F, itemView.context)))
            .into(itemImagePoster)


    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

}