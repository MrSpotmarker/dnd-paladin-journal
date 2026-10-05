package com.paladin.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PaladinGold,
    onPrimary = Color(0xFF14120E),
    primaryContainer = SurfaceCardHighlight,
    onPrimaryContainer = PaladinGoldBright,
    secondary = ChaunteaGreenBright,
    onSecondary = Color(0xFF14120E),
    secondaryContainer = ChaunteaGreenContainer,
    onSecondaryContainer = TextPrimary,
    tertiary = SmiteBlue,
    onTertiary = Color(0xFF14120E),
    background = DarkNavyBackground,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCardHighlight,
    onSurfaceVariant = TextSecondary,
    outline = BorderDark,
    outlineVariant = BorderBrass,
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
