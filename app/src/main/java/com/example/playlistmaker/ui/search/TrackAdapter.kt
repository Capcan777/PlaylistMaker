package com.example.playlistmaker.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.google.android.material.dialog.MaterialDialogs

class TrackAdapter(
    private val clickListener: TrackClickListener
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var tracks = arrayListOf<Track>()

    var onTrackHoldListener: TrackHoldListener? = null

    fun updateTrackList(trackList: ArrayList<Track>) {
        this.tracks = trackList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_items_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(track)
        }
        holder.itemView.setOnLongClickListener {
            onTrackHoldListener?.onLongItemClick(track)
            true
        }

    }

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)

    }

    fun interface TrackHoldListener {
        fun onLongItemClick(track: Track)
    }
}