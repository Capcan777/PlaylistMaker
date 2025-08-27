package com.example.playlistmaker.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Playlist(
    val id: Int,
    val title: String,
    val description: String,
    val pathUrl: String?,
    val numberOfTracks: Int,
    val trackIds: List<Int> = emptyList()
) : Parcelable
