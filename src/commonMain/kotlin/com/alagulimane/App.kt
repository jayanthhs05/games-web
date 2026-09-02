package com.alagulimane

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.alagulimane.ui.screens.GameScreen
import com.alagulimane.ui.screens.HowToPlayScreen
import com.alagulimane.ui.screens.TitleScreen
import com.alagulimane.ui.theme.AlagulimaneTheme
import com.alagulimane.ui.theme.DarkBackground
import com.alagulimane.viewmodel.GameViewModel

/**
 * Navigation screens
 */
enum class Screen {
    TITLE,
    GAME,
    HOW_TO_PLAY
}

/**
 * Root composable. Shared by every platform target; the web target calls this
 * from [main.kt] via ComposeViewport.
 */
@Composable
fun App() {
    AlagulimaneTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            AlagulimaneGame()
        }
    }
}

@Composable
fun AlagulimaneGame() {
    var currentScreen by remember { mutableStateOf(Screen.TITLE) }
    val viewModel = remember { GameViewModel() }

    when (currentScreen) {
        Screen.TITLE -> {
            TitleScreen(
                onPlayClick = { currentScreen = Screen.GAME },
                onHowToPlayClick = { currentScreen = Screen.HOW_TO_PLAY },
                modifier = Modifier.fillMaxSize()
            )
        }
        Screen.GAME -> {
            GameScreen(
                viewModel = viewModel,
                onBackToTitle = { currentScreen = Screen.TITLE },
                modifier = Modifier.fillMaxSize()
            )
        }
        Screen.HOW_TO_PLAY -> {
            HowToPlayScreen(
                onBackClick = { currentScreen = Screen.TITLE },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
