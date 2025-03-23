package com.example.playlistmaker.ui.mediatec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediatecBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediatecFragment : Fragment() {

    private var _binding: FragmentMediatecBinding? = null
    private val binding get() = _binding!!
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediatecBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = MediatecViewPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = lifecycle
        )
        tabLayoutMediator =
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.favorite_tracks)
                    1 -> tab.text = getString(R.string.playlists)
                }
            }
        tabLayoutMediator.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
        _binding = null
    }
}