package com.alagulimane.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alagulimane.model.GameState
import com.alagulimane.model.Player
import com.alagulimane.ui.theme.GoldAccent
import com.alagulimane.ui.theme.Player1Color
import com.alagulimane.ui.theme.Player2Color
import com.alagulimane.ui.theme.TextLight
import com.alagulimane.ui.theme.WoodDark
import com.alagulimane.ui.theme.WoodLight
import com.alagulimane.ui.theme.WoodPrimary
import com.alagulimane.ui.theme.WoodSecondary

/**
 * The main game board - responsive to screen size
 */
@Composable
fun GameBoard(
    gameState: GameState,
    onHoleClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        // Calculate hole size based on available width
        // 7 holes + spacing between them + padding
        val availableWidth = maxWidth
        val spacing = 4.dp
        val boardPadding = 10.dp
        val totalSpacing = spacing * 6 + boardPadding * 2
        val holeSize = ((availableWidth - totalSpacing) / 7).coerceIn(40.dp, 70.dp)
        
        // Wood grain gradient for the board
        val woodGradient = Brush.linearGradient(
            colors = listOf(
                WoodSecondary,
                WoodPrimary,
                WoodLight,
                WoodPrimary,
                WoodSecondary
            )
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Player 2 info (top)
            PlayerInfoBar(
                player = Player.PLAYER_TWO,
                capturedSeeds = gameState.capturedSeeds[Player.PLAYER_TWO] ?: 0,
                isCurrentPlayer = gameState.currentPlayer == Player.PLAYER_TWO,
                seedsInHand = if (gameState.currentPlayer == Player.PLAYER_TWO) gameState.seedsInHand else 0
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // The wooden board
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(woodGradient)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(WoodDark, WoodPrimary, WoodDark)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(boardPadding)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Top row (Player 2's holes: indices 0-6)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        for (i in 0 until 7) {
                            val hole = gameState.holes[i]
                            GameHole(
                                hole = hole,
                                isSelectable = gameState.currentPlayer == Player.PLAYER_TWO && 
                                              !gameState.isGameOver &&
                                              gameState.seedsInHand == 0,
                                isHighlighted = gameState.currentHoleIndex == i,
                                onClick = { onHoleClick(i) },
                                size = holeSize
                            )
                        }
                    }
                    
                    // Bottom row (Player 1's holes: indices 7-13)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        for (i in 7 until 14) {
                            val hole = gameState.holes[i]
                            GameHole(
                                hole = hole,
                                isSelectable = gameState.currentPlayer == Player.PLAYER_ONE && 
                                              !gameState.isGameOver &&
                                              gameState.seedsInHand == 0,
                                isHighlighted = gameState.currentHoleIndex == i,
                                onClick = { onHoleClick(i) },
                                size = holeSize
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Player 1 info (bottom)
            PlayerInfoBar(
                player = Player.PLAYER_ONE,
                capturedSeeds = gameState.capturedSeeds[Player.PLAYER_ONE] ?: 0,
                isCurrentPlayer = gameState.currentPlayer == Player.PLAYER_ONE,
                seedsInHand = if (gameState.currentPlayer == Player.PLAYER_ONE) gameState.seedsInHand else 0
            )
        }
    }
}

/**
 * Player info bar showing name, captured seeds, and turn indicator
 */
@Composable
fun PlayerInfoBar(
    player: Player,
    capturedSeeds: Int,
    isCurrentPlayer: Boolean,
    seedsInHand: Int,
    modifier: Modifier = Modifier
) {
    val playerColor = if (player == Player.PLAYER_ONE) Player1Color else Player2Color
    val playerName = if (player == Player.PLAYER_ONE) "Player 1" else "Player 2"
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isCurrentPlayer) playerColor.copy(alpha = 0.15f) else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = if (isCurrentPlayer) 2.dp else 0.dp,
                color = if (isCurrentPlayer) playerColor else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        // Turn indicator arrow
        if (isCurrentPlayer) {
            Text(
                text = "▶",
                color = playerColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        
        // Player name
        Text(
            text = playerName,
            color = if (isCurrentPlayer) playerColor else TextLight.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Normal
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Captured seeds with seed visual
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = WoodDark.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            // Mini seed visual
            Box(
                modifier = Modifier
                    .size(width = 12.dp, height = 8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF8B5A2B),
                                Color(0xFF5D3A1A),
                                Color(0xFF3D2314)
                            )
                        )
                    )
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = "$capturedSeeds",
                color = GoldAccent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Seeds in hand (during animation)
        if (seedsInHand > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = GoldAccent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Hand:",
                    fontSize = 10.sp,
                    color = TextLight.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "$seedsInHand",
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
