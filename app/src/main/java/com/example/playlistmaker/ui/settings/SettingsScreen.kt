package com.example.playlistmaker.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onUserAgreementClick: () -> Unit
) {
    PlaylistMakerTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Переключатель темы
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onThemeToggle(!isDarkTheme) }
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.set_dark_theme),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onThemeToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            uncheckedTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                        )
                    )
                }



                Spacer(modifier = Modifier.height(16.dp))

                SettingsItem(
                    icon = R.drawable.icon_share,
                    text = stringResource(R.string.share_app),
                    onClick = onShareClick
                )

                SettingsItem(
                    icon = R.drawable.icon_support,
                    text = stringResource(R.string.wright_to_support),
                    onClick = onSupportClick
                )

                SettingsItem(
                    icon = R.drawable.icon_arrow_forward,
                    text = stringResource(R.string.user_agreement),
                    onClick = onUserAgreementClick
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = painterResource(id = icon),
            contentDescription = null
        )
    }
}
