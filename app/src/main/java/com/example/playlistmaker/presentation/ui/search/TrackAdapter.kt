package com.example.playlistmaker.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track

class TrackAdapter(
    private val clickListener: TrackClickListener
//    var trackArrayList: ArrayList<Track>,
//    private val onTrackClicked: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()

    // Метод для обновления списка треков
    fun updateTrackList(trackArrayList: ArrayList<Track>) {
        this.tracks = trackArrayList
        notifyDataSetChanged()  // Уведомляем адаптер о необходимости обновить отображение данных
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
    }
    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}