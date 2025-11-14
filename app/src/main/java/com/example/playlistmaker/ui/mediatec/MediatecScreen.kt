package com.example.playlistmaker.ui.mediatec

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

    // rememberSaveable для selectedTab - это правильно, чтобы сохранять выбранную вкладку
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Ваш код для заголовка и вкладок остается без изменений
        Text(
            text = stringResource(R.string.mediateca),
            modifier = Modifier.padding(start = 12.dp, top = 10.dp, bottom = 12.dp),
            style = MaterialTheme.typography.titleLarge
        )
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = stringResource(R.string.favorite_tracks),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = stringResource(R.string.playlists),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )
        }

        // Единственный и надежный способ интеграции фрагментов
        AndroidView(
            factory = { context ->
                // 1. View создается только один раз
                FragmentContainerView(context).apply {
                    // Генерируем новый ID, который НЕ сохраняется между процессами.
                    // Это предотвращает конфликт при восстановлении состояния.
                    id = ViewCompat.generateViewId()
                }
            },
            update = { container ->
                // 2. Блок 'update' вызывается, когда View готово и при каждой смене 'selectedTab'.
                // Это гарантирует, что FragmentManager и View синхронизированы.
                val fm = hostFragment.childFragmentManager
                val tag = if (selectedTab == 0) FAVORITE_TAG else PLAYLIST_TAG

                // 3. Проверяем, не отображен ли уже нужный фрагмент в этом контейнере.
                // Это предотвращает лишние транзакции при ненужных рекомпозициях.
                if (fm.findFragmentById(container.id)?.tag != tag) {
                    // 4. Если нет, выполняем транзакцию
                    fm.commit {
                        setReorderingAllowed(true)
                        // Пытаемся переиспользовать фрагмент, если он уже есть в менеджере
                        val newFragment = fm.findFragmentByTag(tag)
                            ?: createFragmentForIndex(selectedTab)
                        replace(container.id, newFragment, tag)
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun createFragmentForIndex(index: Int): Fragment {
    return when (index) {
        0 -> FavoriteTrackFragment.newInstance()
        else -> PlaylistsFragment.newInstance()
    }
}

private const val FAVORITE_TAG = "mediatec_favorite"
private const val PLAYLIST_TAG = "mediatec_playlist"
