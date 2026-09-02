package com.alagulimane.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.alagulimane.model.GameAction
import com.alagulimane.model.Player
import com.alagulimane.ui.components.GameBoard
import com.alagulimane.ui.theme.DarkBackground
import com.alagulimane.ui.theme.GoldAccent
import com.alagulimane.ui.theme.GoldDark
import com.alagulimane.ui.theme.Player1Color
import com.alagulimane.ui.theme.Player2Color
import com.alagulimane.ui.theme.TextLight
import com.alagulimane.ui.theme.WoodDark
import com.alagulimane.ui.theme.WoodPrimary
import com.alagulimane.ui.theme.WoodSecondary
import com.alagulimane.viewmodel.GameViewModel

/**
 * Main game screen - proper icons on left, no scrolling
 */
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBackToTitle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            WoodDark.copy(alpha = 0.3f),
            DarkBackground,
            DarkBackground
        )
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Left side - Icon buttons column (top aligned)
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 8.dp, top = 16.dp)
            ) {
                // Undo icon button
                IconButton(
                    onClick = { viewModel.onAction(GameAction.Undo) },
                    enabled = canUndo && !gameState.isAnimating,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (canUndo) WoodPrimary else Color.Black.copy(alpha = 0.4f),
                        disabledContainerColor = Color.Black.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (canUndo) TextLight else TextLight.copy(alpha = 0.3f),
                        modifier = Modifier.size(22.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // New Game icon button
                IconButton(
                    onClick = { viewModel.onAction(GameAction.NewGame) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = WoodPrimary
                    ),
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "New Game",
                        tint = TextLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Menu icon button
                IconButton(
                    onClick = onBackToTitle,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = WoodPrimary
                    ),
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Menu",
                        tint = TextLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            
            // Right side - Game content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Round indicator
                Text(
                    text = "Round ${gameState.roundNumber}",
                    fontSize = 14.sp,
                    color = GoldDark,
                    fontWeight = FontWeight.Medium
                )
                
                // Game board
                GameBoard(
                    gameState = gameState,
                    onHoleClick = { holeIndex ->
                        viewModel.onAction(GameAction.SelectHole(holeIndex))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Status message
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = WoodDark.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = gameState.message,
                        color = GoldAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
                
                // Developer credit
                Text(
                    text = "Developed by: Jayanth",
                    color = TextLight.copy(alpha = 0.4f),
                    fontSize = 10.sp
                )
            }
        }
        
        // Game Over dialog
        if (gameState.isGameOver) {
            GameOverDialog(
                winner = gameState.winner,
                onNewGame = { viewModel.onAction(GameAction.NewGame) },
                onBackToMenu = onBackToTitle
            )
        }
    }
}

/**
 * Dialog shown when the game ends
 */
@Composable
fun GameOverDialog(
    winner: Player?,
    onNewGame: () -> Unit,
    onBackToMenu: () -> Unit
) {
    val winnerName = when (winner) {
        Player.PLAYER_ONE -> "Player 1"
        Player.PLAYER_TWO -> "Player 2"
        null -> "No one"
    }
    
    val winnerColor = when (winner) {
        Player.PLAYER_ONE -> Player1Color
        Player.PLAYER_TWO -> Player2Color
        null -> TextLight
    }
    
    Dialog(onDismissRequest = { }) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = WoodSecondary
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Game Over!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "$winnerName Wins!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = winnerColor
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onNewGame,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldDark
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Play Again",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = onBackToMenu,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Back to Menu",
                        color = TextLight,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
