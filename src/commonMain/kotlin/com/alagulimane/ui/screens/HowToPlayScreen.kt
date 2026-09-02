package com.alagulimane.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alagulimane.ui.theme.DarkBackground
import com.alagulimane.ui.theme.GoldAccent
import com.alagulimane.ui.theme.GoldDark
import com.alagulimane.ui.theme.TextLight
import com.alagulimane.ui.theme.WoodDark
import com.alagulimane.ui.theme.WoodSecondary

/**
 * How to Play screen with game rules - professional formatting
 */
@Composable
fun HowToPlayScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            WoodDark.copy(alpha = 0.3f),
            DarkBackground
        )
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "How to Play",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GoldAccent
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Rules card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = WoodSecondary.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RuleSection(
                    title = "Board Setup",
                    content = "The board consists of two rows with seven holes each. " +
                            "Player 1 controls the bottom row (holes 7-13), and Player 2 controls the top row (holes 0-6). " +
                            "Each hole begins with five seeds, totaling 70 seeds in play."
                )
                
                RuleDivider()
                
                RuleSection(
                    title = "Basic Gameplay",
                    content = "On your turn, select any hole on your side that contains seeds. " +
                            "All seeds from that hole are picked up and distributed one at a time in a counter-clockwise direction. " +
                            "When your hand becomes empty, you pick up all seeds from the next hole and continue sowing."
                )
                
                RuleDivider()
                
                RuleSection(
                    title = "The Karu Rule",
                    content = "If any hole reaches exactly four seeds at any point during play, " +
                            "the owner of that hole immediately captures all four seeds. " +
                            "This applies regardless of which player is currently sowing."
                )
                
                RuleDivider()
                
                RuleSection(
                    title = "Capture (Wipe)",
                    content = "Your turn ends when you drop your last seed into a hole, and the next hole is empty. " +
                            "If the hole after the empty hole contains seeds, you capture those seeds " +
                            "plus any seeds in the hole directly opposite to it."
                )
                
                RuleDivider()
                
                RuleSection(
                    title = "Double Empty Rule",
                    content = "If you land on an empty hole and the next hole is also empty, " +
                            "your turn ends immediately with no capture."
                )
                
                RuleDivider()
                
                RuleSection(
                    title = "End of Round",
                    content = "A round ends when all seeds have been captured from the board. " +
                            "Each player then refills their holes using captured seeds (5 per hole). " +
                            "Any hole that cannot be completely filled becomes a Pauper hole, " +
                            "marked with X and excluded from further play."
                )
                
                RuleDivider()
                
                RuleSection(
                    title = "Winning the Game",
                    content = "The game ends when a player cannot fill any of their holes " +
                            "(becomes a Total Pauper). The opponent wins the game."
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = GoldDark
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Back",
                color = DarkBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun RuleSection(title: String, content: String) {
    Column {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = GoldAccent
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = 13.sp,
            color = TextLight,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun RuleDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = GoldDark.copy(alpha = 0.3f)
    )
}
