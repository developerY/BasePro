package com.ylabz.basepro.feature.weather.ui.components.rain

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.feature.weather.ui.components.backgrounds.Raindrop
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun RainVolumeCard(
    volume: Double,
    dropCount: Int = 300  // Parameterize raindrop count
) {
    val cardHeight = 220.dp
    // Create raindrops with a parameterized count and improved dimensions
    val raindrops = remember { List(dropCount) { Raindrop(1200f, cardHeight.value, 2f..6f, 5f..10f) } }

    LaunchedEffect(Unit) {
        while (true) {
            raindrops.forEach { raindrop ->
                raindrop.move()
                if (raindrop.y > cardHeight.value * 3) {
                    raindrop.resetPosition(cardHeight.value)
                    raindrop.startSplash()
                }
            }
            delay(16)  // Roughly 60 FPS
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .padding(horizontal = 16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB))
    ) {
        // Add a subtle vertical gradient background to the card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFBBDEFB), Color(0xFFE3F2FD))
                    )
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw falling raindrops and splashes
                raindrops.forEach { raindrop ->
                    if (raindrop.isSplashing) {
                        drawCircle(
                            color = Color(0x9E0288D1),
                            radius = raindrop.splashRadius / 2,
                            center = Offset(raindrop.x, size.height - raindrop.splashRadius)
                        )
                    } else {
                        drawLine(
                            color = Color(0x900288D1),
                            start = Offset(raindrop.x, raindrop.y),
                            end = Offset(raindrop.x, raindrop.y + raindrop.size * 4),
                            strokeWidth = 2f
                        )
                    }
                }
            }
            // Overlay the rain volume text with drop shadows for improved legibility.
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rain Volume",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Gray,
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
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


fun ClosedFloatingPointRange<Float>.randomInRange(): Float {
    return Random.nextFloat() * (endInclusive - start) + start
}

@Preview(showBackground = true)
@Composable
fun rainVolumeCardPreview() {
    MaterialTheme {
        RainVolumeCard(volume = 15.0)
    }
}
