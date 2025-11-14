package com.example.playlistmaker.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.ui.search.TracksSearchViewModel
import com.example.playlistmaker.ui.search.components.TrackRow
import com.example.playlistmaker.ui.search.state.SearchScreenState
import com.example.playlistmaker.ui.search.state.TrackUiModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    onTrackClick: (Track) -> Unit,
    viewModel: TracksSearchViewModel = koinViewModel(),
) {
    val screenState by viewModel.screenState.observeAsState(initial = SearchScreenState.Nothing)
    val results by viewModel.searchResults.observeAsState(initial = arrayListOf())

    var query by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = stringResource(R.string.search), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        SearchField(
            value = query,
            onValueChange = {
                query = it
                viewModel.searchDebounce(it.text)
                if (it.text.isEmpty()) {
                    viewModel.updateHistory()
                }
            },
            onClear = {
                query = TextFieldValue("")
                viewModel.updateHistory()
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        when (val state = screenState) {
            is SearchScreenState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SearchScreenState.Tracks -> {
                TrackList(
                    tracks = state.trackSearch,
                    onItemClick = { track ->
                        viewModel.addTrackToHistory(track)
                        onTrackClick(track)
                    }
                )
            }

            is SearchScreenState.History -> {
                HistoryBlock(
                    history = state.historyList,
                    onClear = { viewModel.clearHistory() },
                    onItemClick = { track ->
                        viewModel.addTrackToHistory(track)
                        onTrackClick(track)
                    }
                )
            }

            is SearchScreenState.NetworkError -> {
                Placeholder(
                    imageRes = R.drawable.ic_no_internet,
                    title = stringResource(R.string.no_internet_connection),
                    actionText = stringResource(R.string.update),
                ) {
                    viewModel.searchDebounce(query.text)
                }
            }

            is SearchScreenState.EmptyResult -> {
                Placeholder(
                    imageRes = R.drawable.ic_search_error,
                    title = stringResource(R.string.nothing_found),
                )
            }

            is SearchScreenState.Nothing -> {
                // Пустое состояние – ничего не показываем.
            }
        }
    }
}

@Composable
private fun SearchField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onClear: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val containerColor = if (isDarkTheme) Color.White else Color(0xFFE6E8EB)
    val iconColor = Color(0xFF7E7E92)
    val textColor = Color(0xFF1A1B22)

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            errorContainerColor = containerColor,
            focusedBorderColor = containerColor,
            unfocusedBorderColor = containerColor,
            disabledBorderColor = containerColor,
            errorBorderColor = containerColor,
            focusedLeadingIconColor = iconColor,
            unfocusedLeadingIconColor = iconColor,
            focusedTrailingIconColor = iconColor,
            unfocusedTrailingIconColor = iconColor,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(textColor, 16.sp),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.icon_search_et),
                contentDescription = null
            )
        },
        trailingIcon = {
            if (value.text.isNotEmpty()) {
                ClearIcon(onClick = onClear)
            }
        },
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                style = MaterialTheme.typography.bodyMedium.copy(color = iconColor),
                fontSize = 16.sp,
                fontWeight = FontWeight(400),
                modifier = Modifier.padding(top = 0.dp)
            )
        }
    )
}

@Composable
private fun ClearIcon(onClick: () -> Unit) {
    Icon(
        painter = painterResource(id = R.drawable.icon_close_button),
        contentDescription = null,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        tint = Color(0xFF7E7E92)
    )
}

@Composable
private fun TrackList(
    tracks: List<TrackUiModel>,
    onItemClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(tracks) { uiTrack ->
            TrackRow(
                track = uiTrack.track,
                formattedTime = uiTrack.formattedTime,
                onClick = { onItemClick(uiTrack.track) }
            )
        }
    }
}

@Composable
private fun HistoryBlock(
    history: List<TrackUiModel>,
    onClear: () -> Unit,
    onItemClick: (Track) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.you_searched),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            verticalArrangement = Arrangement.Top
        ) {
            items(history) { uiTrack ->
                TrackRow(
                    track = uiTrack.track,
                    formattedTime = uiTrack.formattedTime,
                    onClick = { onItemClick(uiTrack.track) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onClear,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
                .height(36.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(54.dp)
        ) {
            Text(
                text = stringResource(R.string.to_clear_history),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(500)
                ),
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Composable
private fun Placeholder(
    imageRes: Int,
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = imageRes), contentDescription = null)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
        }
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onAction) {
                Text(text = actionText)
            }
        }
    }
}