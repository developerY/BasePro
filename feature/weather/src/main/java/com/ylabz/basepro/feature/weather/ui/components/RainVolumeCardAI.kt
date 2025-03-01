package com.ylabz.basepro.feature.weather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun RainVolumeCardAI(volume: Double) {
    val cardHeight = 200.dp
    val raindrops = remember { List(300) { Raindrop(1200f, cardHeight.value, 2f..6f, 5f..10f) } }

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
            .shadow(8.dp, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
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
            // Display rain volume text
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rain Volume",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$volume mm",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
            }
        }
    }
}

data class Raindrop(
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

    var isSplashing: Boolean by mutableStateOf(false)
    var splashRadius: Float by mutableStateOf(0f)
    private var splashProgress by mutableStateOf(0f)

    fun move() {
        if (isSplashing) {
            splashProgress += 0.1f
            splashRadius = (splashProgress * size * 3).coerceAtMost(size * 3)
            if (splashProgress >= 1f) {
                isSplashing = false
                splashProgress = 0f
                splashRadius = 0f
            }
        } else {
            y += speed
        }
    }

    fun resetPosition(height: Float) {
        y = -size
        x = random.nextFloat() * screenWidth
        speed = speedRange.randomInRange()
    }

    fun startSplash() {
        isSplashing = true
        splashProgress = 0f
    }
}

fun ClosedFloatingPointRange<Float>.randomInRange(): Float {
    return Random.nextFloat() * (endInclusive - start) + start
}

@Preview(showBackground = true)
@Composable
fun RainVolumeCardPreview() {
    RainVolumeCardAI(volume = 15.0)
}