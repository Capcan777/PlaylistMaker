package com.example.playlistmaker.ui.mediatec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.ui.mediatec.state.PlaylistState
import com.example.playlistmaker.ui.mediatec.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistsFragment()
        }


    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistsViewModel>()
    private val adapter = PlaylistsAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPlaylistItem.layoutManager = GridLayoutManager(requireContext(),2)
        binding.rvPlaylistItem.adapter = adapter
        adapter.onPlaylistClickListener = PlaylistsAdapter.OnPlaylistClickListener { playlist ->
            onPlaylistClick(playlist)
        }

        viewModel.statePlaylistScreen.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Content -> {
                    adapter.setPlaylistList(state.playlist)
                    showContent()
                }

                PlaylistState.Empty -> {
                    adapter.setPlaylistList(emptyList())
                    binding.emptyPlaylistMessage.isVisible = true
                    binding.rvPlaylistItem.isVisible = false
                }

                is PlaylistState.Error -> {}
            }
        }
        viewModel.loadPlaylist()

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediatecFragment_to_newPlaylistFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showContent() = with(binding) {
        rvPlaylistItem.isVisible = true
        newPlaylistButton.isVisible = true
        emptyPlaylistMessage.isVisible = false
        placeholderEmptyPlaylist.isVisible = false
    }

    private fun onPlaylistClick(playlist: Playlist) {
        val bundle = bundleOf("playlist_key" to playlist)
        findNavController().navigate(R.id.action_mediatecFragment_to_playlistFragment, bundle)
    }


}