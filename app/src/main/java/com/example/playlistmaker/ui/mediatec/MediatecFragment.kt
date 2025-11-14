package com.example.playlistmaker.ui.mediatec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

class MediatecFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            PlaylistMakerTheme {
                MediatecScreen(hostFragment = this@MediatecFragment)
            }
        }
    }

    override fun onDestroyView() {
        val transaction = childFragmentManager.beginTransaction()
        childFragmentManager.fragments.forEach { fragment ->
            transaction.remove(fragment)
        }
        transaction.commitNowAllowingStateLoss()
        super.onDestroyView()
    }
}