package com.example.playlistmaker.ui.mediatec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.ui.mediatec.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPlaylists()?.observe(viewLifecycleOwner) {
            showContent()
        }
        binding.newPlaylistButton.visibility = View.VISIBLE
        binding.emptyPlaylistMessage.visibility = View.VISIBLE

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.newPlaylistButton.setOnClickListener {
            navController.navigate(R.id.action_playlistsFragment_to_newPlaylistFragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



//    private fun showErrorMessage() {
//        TODO()
//    }
//
//    private fun showContent() = with(binding) {
//        rvPlaylist.isVisible = true
//        newPlaylistButton.isVisible = true
//        emptyPlaylistMessage.isVisible = false
//        placeholderEmptyPlaylist.isVisible = true
//    }
//
//
//    private fun showErrorMessage() {
//        TODO()
//    }
//
    private fun showContent() {
        TODO()
    }
}