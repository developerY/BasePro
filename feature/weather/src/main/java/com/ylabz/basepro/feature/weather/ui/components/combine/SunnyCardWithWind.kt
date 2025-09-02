package com.ylabz.basepro.feature.weather.ui.components.combine

////import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin

@Composable
fun SunnyCardWithWind(
    temperatureCelsius: Double,
    windDegree: Int,
    modifier: Modifier = Modifier
) {
    // Toggle Celsius/Fahrenheit
    var isCelsius by remember { mutableStateOf(true) }
    val displayedTemp = if (isCelsius) temperatureCelsius else temperatureCelsius * 9 / 5 + 32
    val unit = if (isCelsius) "°C" else "°F"

    Card(
        modifier = modifier
            .fillMaxWidth()//.width(200.dp)
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { isCelsius = !isCelsius },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        // A vertical gradient background (sunny look)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFEE58), Color(0xFFFFCA28))
                    )
                )
                .padding(16.dp)
        ) {
            // Layout everything in a column
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1) Sunshine animation at the top
                Sunshine(
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2) Temperature text
                Text(
                    text = "Sunny",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF424242)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${displayedTemp.roundToLong()} $unit",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = Color(0xFF212121)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3) Wind direction dial
                Text(
                    text = "Wind",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF424242)
                )
                Spacer(modifier = Modifier.height(8.dp))
                WindDirectionDial(
                    degree = windDegree.toFloat(),
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}

@Composable
fun Sunshine(
    modifier: Modifier = Modifier
) {
    // Infinite transitions for rotation & scale
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunRotation"
    )
    val scaleFactor by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunScale"
    )

    // Simple sunshine with rays
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 4 * scaleFactor
            val center = Offset(size.width / 2, size.height / 2)

            // Draw the sun (circle)
            drawCircle(
                color = Color.Yellow,
                center = center,
                radius = radius
            )

            // Draw rays
            repeat(12) { i ->
                rotate(degrees = rotationAngle + i * (360 / 12), pivot = center) {
                    // Each ray is a thin rectangle
                    val rayWidth = 4.dp.toPx()
                    val rayLength = radius * 1.5f
                    drawRoundRect(
                        color = Color.Yellow.copy(alpha = 0.8f),
                        topLeft = Offset(center.x - rayWidth / 2, center.y - radius * 2.5f),
                        size = androidx.compose.ui.geometry.Size(rayWidth, rayLength),
                        cornerRadius = CornerRadius(rayWidth / 2)
                    )
                }
            }
        }
    }
}

@Composable
fun WindDirectionDial(
    degree: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Outer circle
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
            Offset(
                center.x + (arrowLength * cos(arrowAngleRad)).toFloat(),
                center.y + (arrowLength * sin(arrowAngleRad)).toFloat()
            )

            // Slight translation to center the arrow's base
            translate(left = center.x, top = center.y) {
                rotate(degrees = degree - 90) {
                    // Arrow shaft
                    drawRoundRect(
                        color = Color.Red,
                        topLeft = Offset(-3f, -arrowLength),
                        size = androidx.compose.ui.geometry.Size(6f, arrowLength),
                        cornerRadius = CornerRadius(3f)
                    )
                    // Arrow head
                    drawRoundRect(
                        color = Color.Red,
                        topLeft = Offset(-6f, -arrowLength - 10f),
                        size = androidx.compose.ui.geometry.Size(12f, 10f),
                        cornerRadius = CornerRadius(3f)
                    )
                }
            }
        }
    }
}
/*
@Preview
@Composable
fun SunnyCardWithWindPreview() {
    val temperatureCelsius = 25.0
    val windDegree = 180
    SunnyCardWithWind(temperatureCelsius, windDegree)
}
*/

