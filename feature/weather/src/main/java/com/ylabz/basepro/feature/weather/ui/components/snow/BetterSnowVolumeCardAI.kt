package com.ylabz.basepro.feature.weather.ui.components.snow

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun BetterSnowVolumeCardAI(volume: Double) {
    // Increase card height for a bit more room
    val cardHeight = 220.dp
    // Initial accumulated snow height (in pixels) for simulation
    val initialAccumulatedSnow = 20f
    val accumulatedSnowHeight = remember { mutableStateOf(initialAccumulatedSnow) }
    // Create a parameterized list of snowflakes (density adjustable)
    val snowflakes = remember { List(500) { Snowflake(1200f, cardHeight.value, 1f..2.5f, 1f..5f) } }

    // Animate the snow: move each snowflake and accumulate when it resets
    LaunchedEffect(Unit) {
        while (true) {
            snowflakes.forEach { snowflake ->
                snowflake.move()
                if (snowflake.y > cardHeight.value * 3) {
                    // Increase accumulated snow slightly based on snowflake size
                    accumulatedSnowHeight.value += snowflake.size / 10f
                    snowflake.resetPosition(cardHeight.value)
                }
            }
            delay(16L)  // Roughly 60 FPS
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .padding(horizontal = 16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Use a subtle vertical gradient for a wintry background
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF90CAF9), Color(0xFFBBDEFB))
                    )
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw accumulated snow as a soft, semi-transparent layer at the bottom
                drawRect(
                    color = Color.White.copy(alpha = 0.9f),
                    topLeft = Offset(0f, size.height - accumulatedSnowHeight.value),
                    size = Size(size.width, accumulatedSnowHeight.value)
                )
                // Draw falling snowflakes
                snowflakes.forEach { snowflake ->
                    drawCircle(
                        color = Color.White,
                        radius = snowflake.size,
                        center = Offset(snowflake.x, snowflake.y)
                    )
                }
            }
            // Overlay the card with text showing snow volume
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Snow Volume",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Gray,
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$volume mm",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        shadow = Shadow(
                            color = Color.Gray,
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    color = Color.Black
                )
            }
        }
    }
}

data class Snowflake(
    val screenWidth: Float,
    val screenHeight: Float,
    val sizeRange: ClosedFloatingPointRange<Float>,
    val speedRange: ClosedFloatingPointRange<Float>
) {
    private val random = Random(System.nanoTime())
    val size: Float = sizeRange.randomInRange()
    var speed: Float = speedRange.randomInRange()
    var x: Float = random.nextFloat() * screenWidth
    var y: Float = random.nextFloat() * screenHeight

    fun move() {
        y += speed
    }

    fun resetPosition(height: Float) {
        y = -size
        x = random.nextFloat() * screenWidth
        speed = speedRange.randomInRange()
    }
}

fun ClosedFloatingPointRange<Float>.randomInRange(): Float {
    return Random.nextFloat() * (endInclusive - start) + start
}

@Preview(showBackground = true)
@Composable
fun BetterSnowVolumeCardPreview() {
    MaterialTheme {
        BetterSnowVolumeCardAI(volume = 10.0)
    }
}
