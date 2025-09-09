package com.example.playlistmaker.data.sharing.model

import com.example.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

data class TrackDataSharing(
    val playlistTitle: String?,
    val playlistDescription: String?,
    val playlistTrackList: List<Track>,
    val trackAmount: Int
) {
    override fun toString(): String = buildString {
        val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

        appendLine(playlistTitle.orEmpty())
        if (!playlistDescription.isNullOrBlank()) {
            appendLine(playlistDescription)
        }
        appendLine(trackAmount.getTrackString())

        playlistTrackList.forEachIndexed { index, track ->
            val time = timeFormatter.format(track.trackTimeMillis)
            appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${time})")
        }
    }

    private fun Int.getTrackString(): String {
        return when {
            this % 100 in 11..14 -> "$this треков"
            this % 10 == 1 -> "$this трек"
            this % 10 in 2..4 -> "$this трека"
            else -> "$this треков"
        }
    }
}
