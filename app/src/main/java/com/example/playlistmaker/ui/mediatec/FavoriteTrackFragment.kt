package com.example.playlistmaker.ui.mediatec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoriteTrackBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoriteTrackFragment : Fragment() {

    companion object {
        fun newInstance() = FavoriteTrackFragment()
    }

    private lateinit var binding: FragmentFavoriteTrackBinding
    private val viewModel by viewModel<FavoriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteTrackBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFavorites()?.observe(viewLifecycleOwner) {
            showContent(it)
        }
    }

    private fun showContent(content: Any?) {
        TODO()
    }


}

