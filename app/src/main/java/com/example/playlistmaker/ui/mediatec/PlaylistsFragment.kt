package com.example.playlistmaker.ui.mediatec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private lateinit var binding: FragmentPlaylistsBinding
    private val viewModel by viewModel<PlaylistViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPlaylists()?.observe(viewLifecycleOwner) {
            showContent()
        }
        binding.newPlaylistButton.visibility = View.VISIBLE
        binding.emptyPlaylistMessage.visibility = View.VISIBLE
    }


    private fun showErrorMessage() {
        TODO()
    }

    private fun showContent() {
        TODO()
    }
}