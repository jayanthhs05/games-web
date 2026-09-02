package com.alagulimane.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = WoodPrimary,
    secondary = WoodSecondary,
    tertiary = GoldAccent,
    background = DarkBackground,
    surface = SurfaceDark,
    onPrimary = TextLight,
    onSecondary = TextLight,
    onTertiary = TextDark,
    onBackground = TextLight,
    onSurface = TextLight
)

@Composable
fun AlagulimaneTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
