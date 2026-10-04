package com.paladin.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PaladinGold,
    onPrimary = Color.Black,
    primaryContainer = SurfaceCardHighlight,
    onPrimaryContainer = PaladinGold,
    secondary = SmiteBlue,
    onSecondary = Color.Black,
    background = DarkNavyBackground,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCardHighlight,
    onSurfaceVariant = TextSecondary,
    outline = BorderDark,
    error = HealthRed,
    onError = Color.White
)

@Composable
fun PaladinAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
