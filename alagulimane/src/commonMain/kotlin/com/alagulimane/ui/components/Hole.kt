package com.alagulimane.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alagulimane.model.Hole
import com.alagulimane.model.Player
import com.alagulimane.ui.theme.GoldAccent
import com.alagulimane.ui.theme.Player1Color
import com.alagulimane.ui.theme.Player2Color
import com.alagulimane.ui.theme.TextLight
import com.alagulimane.ui.theme.WoodDark
import com.alagulimane.ui.theme.WoodDeep

/**
 * A single hole on the game board - responsive sizing
 */
@Composable
fun GameHole(
    hole: Hole,
    isSelectable: Boolean,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 50.dp
) {
    val holeGradient = Brush.radialGradient(
        colors = if (hole.isPauper) {
            listOf(
                Color(0xFF1A1A1A),
                Color(0xFF2D2D2D),
                Color(0xFF3D3D3D)
            )
        } else {
            listOf(
                WoodDeep,
                WoodDark,
                WoodDark.copy(alpha = 0.8f)
            )
        }
    )
    
    val borderColor = when {
        isHighlighted -> GoldAccent
        isSelectable && hole.seedCount > 0 -> {
            if (hole.owner == Player.PLAYER_ONE) Player1Color.copy(alpha = 0.8f)
            else Player2Color.copy(alpha = 0.8f)
        }
        else -> Color.Transparent
    }
    
    // Scale font size based on hole size
    val countFontSize = (size.value * 0.18f).sp
    val pauperFontSize = (size.value * 0.3f).sp
    
    // Column to stack hole and count vertically
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // The circular hole
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = if (hole.isPauper) 2.dp else 4.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .clip(CircleShape)
                .background(holeGradient)
                .border(
                    width = if (borderColor != Color.Transparent) 2.dp else 0.dp,
                    color = borderColor,
                    shape = CircleShape
                )
                .clickable(enabled = isSelectable && !hole.isPauper && hole.seedCount > 0) {
                    onClick()
                }
        ) {
            // Seeds inside the hole
            if (!hole.isPauper && hole.seedCount > 0) {
                SeedCluster(
                    seedCount = hole.seedCount,
                    containerSize = size * 0.75f
                )
            }
            
            // Pauper indicator
            if (hole.isPauper) {
                Text(
                    text = "✕",
                    color = Color.Gray.copy(alpha = 0.5f),
                    fontSize = pauperFontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Seed count displayed BELOW the hole
        if (!hole.isPauper) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(3.dp)
                    )
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "${hole.seedCount}",
                    color = if (hole.seedCount > 0) GoldAccent else TextLight.copy(alpha = 0.5f),
                    fontSize = countFontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
