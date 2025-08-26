package com.example.playlistmaker.ui.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.BottomSheetItemBinding
import com.example.playlistmaker.domain.model.Playlist

class PlayerPlaylistAdapter() : RecyclerView.Adapter<PlayerPlaylistViewHolder>() {

    private var playlists = mutableListOf<Playlist>()

    fun setPlaylists(list: List<Playlist>) {
        playlists.clear()
        playlists.addAll(list)
        notifyDataSetChanged()
    }

    fun incrementTracksCountFor(playlistId: Int, trackId: Int) {
        val index = playlists.indexOfFirst { it.id == playlistId }
        if (index != -1) {
            val updated = playlists[index].copy(
                numberOfTracks = playlists[index].numberOfTracks + 1,
                trackIds = playlists[index].trackIds + trackId
            )
            playlists[index] = updated
            notifyItemChanged(index)
        }
    }

    var onPlaylistClickListener: OnPlaylistClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayerPlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlayerPlaylistViewHolder(BottomSheetItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun onBindViewHolder(holder: PlayerPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onPlaylistClickListener?.onPlaylistClick(playlists[position])
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
    fun interface OnPlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}