package com.example.playlistmaker.ui.mediatec

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.domain.model.Playlist

class PlaylistsAdapter() : RecyclerView.Adapter<PlaylistsViewHolder>() {

    private var playlists = mutableListOf<Playlist>()

    fun setPlaylistList(list: List<Playlist>) {
        playlists.clear()
        playlists.addAll(list)
        notifyDataSetChanged()
    }

    var onPlaylistClickListener: OnPlaylistClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistsViewHolder(PlaylistItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            Log.d("PlaylistsAdapter", "Clicked on position: $position")
            onPlaylistClickListener?.onPlaylistClick(playlists[position])
        }
    }

    fun interface OnPlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}