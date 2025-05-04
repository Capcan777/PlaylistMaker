package com.example.playlistmaker.ui.mediatec

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.playlistmaker.databinding.FragmentFavoriteTrackBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.mediatec.state.FavoriteState
import com.example.playlistmaker.ui.mediatec.view_model.FavoriteViewModel
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.search.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.ui.search.SearchFragment.Companion.TRACK_INTENT
import com.example.playlistmaker.ui.search.TrackAdapter
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoriteTrackFragment : Fragment() {

    companion object {

        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val TRACK_INTENT = "track_intent"

        fun newInstance() = FavoriteTrackFragment()

    }

    private var isClickAllowed = true

    private lateinit var favoriteAdapter: TrackAdapter


    private var _binding: FragmentFavoriteTrackBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FavoriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteTrackBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteAdapter = TrackAdapter { track ->
            onTrackSelected(track)
        }
        binding.rvFavorites.adapter = favoriteAdapter

        viewModel.getFavorites()?.observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.loadFavoriteTracks()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavoriteTracks()
        favoriteAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Content -> {
                showContent()
                favoriteAdapter.updateTrackList(ArrayList(state.favoriteList))
            }
            is FavoriteState.Empty -> showEmpty()
        }
    }

    private fun showContent() {
        binding.messageEmptyMediatec.visibility = View.GONE
        binding.rvFavorites.visibility = View.VISIBLE

    }

    private fun showEmpty() {
        binding.messageEmptyMediatec.visibility = View.VISIBLE
        binding.rvFavorites.visibility = View.GONE
    }

    private fun onTrackSelected(track: Track) {
        if (clickDebounce()) {
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            playerIntent.putExtra(TRACK_INTENT, Gson().toJson(track))
            startActivity(playerIntent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current: Boolean = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }


}
