package com.ylabz.basepro.feature.weather.ui.components.sun

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import kotlin.math.roundToLong
//import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TemperatureCardAI(temp: Double) {
    // Toggle between Celsius and Fahrenheit
    var isCelsius by remember { mutableStateOf(true) }
    val temperature = if (isCelsius) temp else temp * 9 / 5 + 32
    val unit = if (isCelsius) "°C" else "°F"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { isCelsius = !isCelsius },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            // Use a horizontal gradient for a modern, dynamic background
            containerColor = Color.Unspecified
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFFB74D), Color(0xFFFFA726))
                    )
                )
        ) {
            // Animated Sunshine in the top-left as a decorative accent
            Sunshine(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                rotationAngle = rememberInfiniteTransition().animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 12000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotAng"
                ).value,
                scaleFactor = rememberInfiniteTransition().animateFloat(
                    initialValue = 1f,
                    targetValue = 1.3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 2500, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scaleFac"
                ).value,
                breakoutDistance = rememberInfiniteTransition().animateFloat(
                    initialValue = 0f,
                    targetValue = 800f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 5000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "breakoutDis"
                ).value,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Temperature",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = androidx.compose.ui.unit.IntOffset(2, 2).let { Offset(it.x.toFloat(), it.y.toFloat()) },
                            blurRadius = 4f
                        )
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${temperature.roundToLong()} $unit",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 42.sp,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.4f),
                            offset = Offset(2f, 2f),
                            blurRadius = 6f
                        )
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Replace or implement your thermometer here.
                Thermometer(temperature = temperature.toFloat())
            }
        }
    }
}

// Dummy Sunshine composable (Replace with your actual implementation)
@Composable
fun Sunshine(
    modifier: Modifier = Modifier,
    rotationAngle: Float,
    scaleFactor: Float,
    breakoutDistance: Float,
) {
    // For now, just a placeholder circle to represent the sun.
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.Yellow,
                radius = size.minDimension / 2 * scaleFactor
            )
        }
    }
}

// Dummy Thermometer composable placeholder.
@Composable
fun Thermometer(temperature: Float) {
    Box(
        modifier = Modifier
            .size(width = 40.dp, height = 120.dp)
            .background(Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.BottomCenter
    ) {
        // A simple fill representing the temperature (for demonstration)
        val fillHeight = (temperature / 50f).coerceIn(0f, 1f) * 120.dp.value
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(fillHeight.dp)
                .background(Color.Red, shape = RoundedCornerShape(20.dp))
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun TemperatureCardPreview() {
    MaterialTheme {
        TemperatureCardAI(temp = 22.0)
    }
}*/
