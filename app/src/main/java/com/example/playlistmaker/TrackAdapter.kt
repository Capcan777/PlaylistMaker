package com.example.playlistmaker

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {

    private lateinit var sharedPreferences: SharedPreferences
    var trackArrayList: ArrayList<Track> = arrayListOf()

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
            val searchHistory = SearchHistory(sharedPreferences)

            // Чтение текущей истории поиска
            val currentHistory = searchHistory.readTracksFromHistory()

            // Удаление трека из истории, если он уже существует (по уникальному идентификатору)
            currentHistory.removeAll { it.trackId == track.trackId }

            // Добавление трека в начало списка
            currentHistory.add(0, track)
            notifyDataSetChanged()

            // Установка ограничения списка треков в истории
            if (currentHistory.size > 10) {
                currentHistory.removeAt(10)
                notifyItemRemoved(10)
                notifyItemRangeChanged(0, currentHistory.size - 1)
            }

            // Сохранение обновленной истории
            searchHistory.saveTrackToHistory(currentHistory)
        }
    }
}