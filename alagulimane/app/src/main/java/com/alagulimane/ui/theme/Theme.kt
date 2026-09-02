package com.alagulimane.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = WoodDeep.toArgb()
            window.navigationBarColor = WoodDeep.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
