package com.song.ply.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Purple,
    onPrimary = White,
    secondary = Black,
    background = White,
    onBackground = Black,
    error = Red,
    tertiary = LightGrey,
    onTertiary = DarkGrey,
    surface = White,
    onSurface = Black
)

private val LightColorScheme = lightColorScheme(
    primary = Purple,
    onPrimary = White,
    secondary = Black,
    background = White,
    onBackground = Black,
    error = Red,
    tertiary = LightGrey,
    onTertiary = DarkGrey,
    surface = White,
    onSurface = Black
)

@Composable
fun SongPlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}