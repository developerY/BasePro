package com.ylabz.basepro.feature.weather.ui.components.cloud

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToLong
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.feature.weather.ui.components.combine.WindDirectionDialWithSpeed

@Composable
fun CloudyWeatherCardWithMotionAndWind(
    temperature: Double,
    location: String,
    windDegree: Int,
    windSpeed: Float,
    modifier: Modifier = Modifier
) {
    val cardHeight = 200.dp

    // Infinite transition for subtle cloud motion.
    val infiniteTransition = rememberInfiniteTransition()
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB0BEC5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFCFD8DC), Color(0xFFB0BEC5))
                    )
                )
        ) {
            // Cloud icon in the top-left
            Icon(
                imageVector = Icons.Filled.Cloud,
                contentDescription = "Cloudy",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            )
            // Animated clouds drawn on a Canvas with horizontal motion
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = 30.dp.toPx(),
                    center = Offset(
                        x = size.width * 0.7f + cloudOffset,
                        y = size.height * 0.3f
                    )
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.4f),
                    radius = 20.dp.toPx(),
                    center = Offset(
                        x = size.width * 0.6f + cloudOffset * 0.5f,
                        y = size.height * 0.4f
                    )
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.45f),
                    radius = 25.dp.toPx(),
                    center = Offset(
                        x = size.width * 0.8f + cloudOffset * 1.5f,
                        y = size.height * 0.35f
                    )
                )
            }
            // Centered text: temperature, condition, location
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${temperature.roundToLong()}°C",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 36.sp,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cloudy",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
            // Wind dial with wind speed, positioned in the bottom-left corner.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                WindDirectionDialWithSpeed(
                    degree = windDegree.toFloat(),
                    speed = windSpeed
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CloudyWeatherCardWithMotionAndWindPreview() {
    MaterialTheme {
        CloudyWeatherCardWithMotionAndWind(
            temperature = 18.0,
            location = "Seattle, WA",
            windDegree = 60,
            windSpeed = 5f
        )
    }
}
