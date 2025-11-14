package com.example.playlistmaker.ui.mediatec

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import com.example.playlistmaker.R

@Composable
fun MediatecScreen(hostFragment: Fragment) {
    val tabs = remember { MediatecTab.values().toList() }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(MediatecTab.FAVORITES.ordinal) }
    val selectedTab = tabs[selectedTabIndex]
    val containerId by rememberSaveable { mutableIntStateOf(ViewCompat.generateViewId()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = stringResource(R.string.mediateca),
            modifier = Modifier.padding(start = 12.dp, top = 10.dp, bottom = 12.dp),
            style = MaterialTheme.typography.titleLarge
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = stringResource(tab.titleRes),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
        }

        AndroidView(
            factory = { context ->
                FragmentContainerView(context).apply {
                    id = containerId
                }
            },
            update = { container ->
                if (container.id != containerId) {
                    container.id = containerId
                }
                val fragmentManager = hostFragment.childFragmentManager

                if (fragmentManager.findFragmentById(containerId)?.tag != selectedTab.tag) {
                    fragmentManager.commit {
                        setReorderingAllowed(true)
                        val fragment = fragmentManager.findFragmentByTag(selectedTab.tag)
                            ?: selectedTab.createFragment()
                        replace(containerId, fragment, selectedTab.tag)
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private enum class MediatecTab(
    @StringRes val titleRes: Int,
    val tag: String
) {
    FAVORITES(R.string.favorite_tracks, FAVORITE_TAG) {
        override fun createFragment(): Fragment = FavoriteTrackFragment.newInstance()
    },
    PLAYLISTS(R.string.playlists, PLAYLIST_TAG) {
        override fun createFragment(): Fragment = PlaylistsFragment.newInstance()
    };

    abstract fun createFragment(): Fragment
}

private const val FAVORITE_TAG = "mediatec_favorite"
private const val PLAYLIST_TAG = "mediatec_playlist"
