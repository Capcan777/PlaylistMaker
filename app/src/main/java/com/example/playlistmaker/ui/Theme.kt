package com.example.playlistmaker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

// === Цвета ===
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3772E7), // dark_blue
    onPrimary = Color.White,
    background = Color(0xFF1A1B22), // dark_grey
    onBackground = Color.White,
    surface = Color(0xFF1A1B22),
    onSurface = Color.White,
    secondary = Color(0xFF1A1B22),
    onSecondary = Color(0xFF1A1B22),
    primaryContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3772E7),
    onPrimary = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1A1B22),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1B22),
    secondary = Color(0xFF1A1B22),
    onSecondary = Color(0xFFAEAFB4),
    primaryContainer = Color(0xFFE6E8EB)
)

private val YsDisplay = FontFamily(
    Font(R.font.ys_display_medium),
    Font(R.font.yandex_sans_display_regular),
    Font(R.font.yandex_sans_display_bold)
)
private val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = YsDisplay,
        fontSize = 22.sp,
        fontWeight = FontWeight(500),
    ),
    bodyLarge = TextStyle(
        fontFamily = YsDisplay,
        fontSize = 19.sp,
        fontWeight = FontWeight(500)
    ),
    bodySmall = TextStyle(
        fontFamily = YsDisplay,
        fontSize = 14.sp,
        fontWeight = FontWeight(500)
    ),
    bodyMedium = TextStyle(
        fontFamily = YsDisplay,
        fontSize = 16.sp, fontWeight = FontWeight(400))
)

// === Основная тема приложения ===
@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(
            titleLarge = MaterialTheme.typography.titleLarge.copy(
                color = colors.onBackground
            ),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                color = colors.onBackground
            ),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                color = colors.onBackground
            )
        ),
        content = content
    )
}
