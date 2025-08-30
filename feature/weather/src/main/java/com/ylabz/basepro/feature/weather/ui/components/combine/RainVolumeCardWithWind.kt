package com.ylabz.basepro.feature.weather.ui.components.combine

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Color
////import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.feature.weather.ui.components.backgrounds.Raindrop
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun RainVolumeCardWithWind(
    volume: Double,
    windDegree: Int,
    modifier: Modifier = Modifier
) {
    val cardHeight = 200.dp
    // Create some raindrops
    val raindrops = remember { List(300) { Raindrop(1200f, cardHeight.value, 2f..6f, 5f..10f) } }

    // Animate raindrops in a loop
    LaunchedEffect(Unit) {
        while (true) {
            raindrops.forEach { raindrop ->
                raindrop.move()
                if (raindrop.y > cardHeight.value * 3) {
                    raindrop.resetPosition(cardHeight.value)
                    raindrop.startSplash()
                }
            }
            delay(16)  // ~60 FPS
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .padding(horizontal = 16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Draw the raindrops
            Canvas(modifier = Modifier.fillMaxSize()) {
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

            // Overlay text and wind dial
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Rain volume text
                Text(
                    text = "Rain Volume",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${volume} mm",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Wind direction label + dial
                Text(
                    text = "Wind Direction",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                WindDirectionDialOn(
                    degree = windDegree.toFloat(),
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}


// A simple wind dial composable
@Composable
fun WindDirectionDialOn(
    degree: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Light circle background
            drawCircle(
                color = Color(0xFFBBDEFB),
                center = center,
                radius = radius
            )

            // Markers every 45° (N, NE, E, SE, etc.)
            for (i in 0..360 step 45) {
                val angleRad = Math.toRadians((i - 90).toDouble())
                val markerStart = Offset(
                    center.x + (radius * 0.9f * cos(angleRad)).toFloat(),
                    center.y + (radius * 0.9f * sin(angleRad)).toFloat()
                )
                val markerEnd = Offset(
                    center.x + (radius * cos(angleRad)).toFloat(),
                    center.y + (radius * sin(angleRad)).toFloat()
                )
                drawLine(
                    color = Color.DarkGray,
                    start = markerStart,
                    end = markerEnd,
                    strokeWidth = 2f
                )
            }

            // Red arrow for wind direction
            val arrowLength = radius * 0.6f
            val arrowAngleRad = Math.toRadians((degree - 90).toDouble())

            val tipX = center.x + (arrowLength * cos(arrowAngleRad)).toFloat()
            val tipY = center.y + (arrowLength * sin(arrowAngleRad)).toFloat()

            // Arrow shaft
            drawLine(
                color = Color.Red,
                start = center,
                end = Offset(tipX, tipY),
                strokeWidth = 4f
            )

            // Simple arrow head
            val headSize = 10f
            val leftAngle = arrowAngleRad + Math.toRadians(150.0)
            val rightAngle = arrowAngleRad - Math.toRadians(150.0)

            val leftX = tipX + headSize * cos(leftAngle).toFloat()
            val leftY = tipY + headSize * sin(leftAngle).toFloat()
            val rightX = tipX + headSize * cos(rightAngle).toFloat()
            val rightY = tipY + headSize * sin(rightAngle).toFloat()

            drawLine(
                color = Color.Red,
                start = Offset(tipX, tipY),
                end = Offset(leftX, leftY),
                strokeWidth = 4f
            )
            drawLine(
                color = Color.Red,
                start = Offset(tipX, tipY),
                end = Offset(rightX, rightY),
                strokeWidth = 4f
            )
        }
    }
}
/*
/*@Preview(showBackground = true)
@Composable
fun RainVolumeCardWithWindPreview() {
    RainVolumeCardWithWind(volume = 15.0, windDegree = 45)
}
*/