package com.example.playlistmaker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            PlaylistMakerTheme(darkTheme = isDarkTheme) {
                SettingsScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isChecked ->
                        viewModel.onThemeSwitched(isChecked)
                        (requireActivity().application as? App)?.switchTheme(isChecked)
                    },
                    onShareClick = { viewModel.shareLink() },
                    onSupportClick = { viewModel.openSupport() },
                    onUserAgreementClick = { viewModel.openTerms() }
                )
            }
        }
    }
}
