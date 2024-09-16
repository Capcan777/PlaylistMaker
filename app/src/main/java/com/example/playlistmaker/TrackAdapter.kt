package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class TrackAdapter(
    var trackArrayList: ArrayList<Track>,
    private val onTrackClicked: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    private lateinit var sharedPreferences: SharedPreferences


    // Метод для установки SharedPreferences
    fun setSharedPreferences(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
    }

    // Метод для обновления списка треков
    fun updateTrackList(trackArrayList: ArrayList<Track>) {
        this.trackArrayList = trackArrayList
        notifyDataSetChanged()  // Уведомляем адаптер о необходимости обновить отображение данных
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_items_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trackArrayList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackArrayList[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            onTrackClicked(track)
            val playerIntent = Intent(holder.itemView.context, PlayerActivity::class.java)
            playerIntent.putExtra("track", Gson().toJson(track))
            holder.itemView.context.startActivity(playerIntent)
        }
    }
}