package com.alagulimane.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

// Tamarind seed colors - rich brown tones
private val TamarindDark = Color(0xFF3D2314)      // Dark brown shell
private val TamarindMid = Color(0xFF5D3A1A)       // Medium brown
private val TamarindLight = Color(0xFF8B5A2B)     // Light brown highlight
private val TamarindShine = Color(0xFFB8860B)     // Golden shine spot

/**
 * A single tamarind seed with realistic oval shape and brown coloring
 */
@Composable
fun Seed(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    randomSeed: Int = 0
) {
    val random = remember(randomSeed) { Random(randomSeed) }
    val sizeVariation = remember(randomSeed) { 0.9f + random.nextFloat() * 0.2f }
    val rotation = remember(randomSeed) { -30f + random.nextFloat() * 60f }
    
    // Realistic tamarind seed gradient - darker at edges, lighter center
    val seedGradient = Brush.verticalGradient(
        colors = listOf(
            TamarindLight,
            TamarindMid,
            TamarindDark
        )
    )
    
    val actualWidth = (size.value * sizeVariation * 1.6f).dp
    val actualHeight = (size.value * sizeVariation).dp
    
    Box(
        modifier = modifier
            .size(width = actualWidth, height = actualHeight)
            .rotate(rotation)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(50),
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = Color.Black
            )
            .clip(RoundedCornerShape(50))
            .background(seedGradient)
            .border(
                width = 0.5.dp,
                color = TamarindDark.copy(alpha = 0.7f),
                shape = RoundedCornerShape(50)
            )
    ) {
        // Add a subtle shine highlight
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 2.dp, y = 2.dp)
                .size(width = actualWidth * 0.3f, height = actualHeight * 0.4f)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            TamarindShine.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/**
 * Renders multiple seeds in a neat grid pattern within a hole
 */
@Composable
fun SeedCluster(
    seedCount: Int,
    containerSize: Dp,
    modifier: Modifier = Modifier
) {
    if (seedCount == 0) return
    
    // Adaptive seed size based on count
    val seedSize = when {
        seedCount <= 2 -> 18.dp
        seedCount <= 4 -> 16.dp
        seedCount <= 6 -> 14.dp
        seedCount <= 9 -> 12.dp
        seedCount <= 12 -> 10.dp
        else -> 8.dp
    }
    
    // Calculate grid layout
    val positions = remember(seedCount, containerSize) {
        val random = Random(seedCount * 100)
        val centerX = containerSize.value / 2
        val centerY = containerSize.value / 2
        val spreadRadius = containerSize.value * 0.35f
        
        when (seedCount) {
            1 -> listOf(Pair(0f, 0f))
            2 -> listOf(Pair(-8f, 0f), Pair(8f, 0f))
            3 -> listOf(
                Pair(0f, -8f),
                Pair(-8f, 6f), Pair(8f, 6f)
            )
            4 -> listOf(
                Pair(-8f, -8f), Pair(8f, -8f),
                Pair(-8f, 8f), Pair(8f, 8f)
            )
            5 -> listOf(
                Pair(0f, -10f),
                Pair(-10f, 0f), Pair(10f, 0f),
                Pair(-6f, 10f), Pair(6f, 10f)
            )
            else -> {
                // Circular arrangement for more seeds
                (0 until seedCount).map { i ->
                    val angle = (i * 360f / seedCount) + random.nextFloat() * 20f
                    val r = if (i < seedCount / 2) spreadRadius * 0.5f else spreadRadius
                    val jitter = random.nextFloat() * 4f - 2f
                    val x = (r * kotlin.math.cos(Math.toRadians(angle.toDouble()))).toFloat() + jitter
                    val y = (r * kotlin.math.sin(Math.toRadians(angle.toDouble()))).toFloat() + jitter
                    Pair(x, y)
                }
            }
        }
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(containerSize)
    ) {
        positions.forEachIndexed { index, (offsetX, offsetY) ->
            Seed(
                size = seedSize,
                randomSeed = index + seedCount * 10,
                modifier = Modifier.offset(x = offsetX.dp, y = offsetY.dp)
            )
        }
    }
}
