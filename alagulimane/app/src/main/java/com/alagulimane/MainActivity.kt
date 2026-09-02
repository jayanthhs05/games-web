package com.alagulimane

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alagulimane.ui.screens.GameScreen
import com.alagulimane.ui.screens.HowToPlayScreen
import com.alagulimane.ui.screens.TitleScreen
import com.alagulimane.ui.theme.AlagulimaneTheme
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
 * Main activity for the Alagulimane game
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            AlagulimaneTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    AlagulimaneApp()
                }
            }
        }
    }
}

@Composable
fun AlagulimaneApp() {
    var currentScreen by remember { mutableStateOf(Screen.TITLE) }
    val viewModel: GameViewModel = viewModel()
    
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
