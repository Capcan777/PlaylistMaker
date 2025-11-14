package com.example.playlistmaker.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.ui.search.TracksSearchViewModel
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private val viewModel by viewModel<TracksSearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            PlaylistMakerTheme {
                SearchScreen(
                    onTrackClick = { track -> openPlayer(track) }
                )
            }
        }
    }

    companion object {
        const val TRACK_INTENT = "track_intent"
    }

    private fun openPlayer(track: Track) {
        val playerIntent = android.content.Intent(requireContext(), PlayerActivity::class.java)
        playerIntent.putExtra(TRACK_INTENT, Gson().toJson(track))
        startActivity(playerIntent)
    }
}

