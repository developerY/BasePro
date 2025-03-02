package com.ylabz.basepro.feature.weather.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

@Composable
fun CloudyWeatherCardWithMotion(
    temperature: Double,
    location: String,
    modifier: Modifier = Modifier
) {
    val cardHeight = 200.dp

    // Infinite transition for subtle cloud motion.
    val infiniteTransition = rememberInfiniteTransition()
    // Animate a cloud offset that oscillates between -10 and +10 pixels over 6000ms.
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 6000, easing = LinearEasing),
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
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFB0BEC5) // Base blue-grey tone
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Vertical gradient for a cloudy feel
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
            // Animated clouds drawn on the Canvas with subtle horizontal motion
            Canvas(modifier = Modifier.fillMaxSize()) {
                // First cloud: base position + full offset
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = 30.dp.toPx(),
                    center = Offset(
                        x = size.width * 0.7f + cloudOffset,
                        y = size.height * 0.3f
                    )
                )
                // Second cloud: base position + half the offset
                drawCircle(
                    color = Color.White.copy(alpha = 0.4f),
                    radius = 20.dp.toPx(),
                    center = Offset(
                        x = size.width * 0.6f + cloudOffset * 0.5f,
                        y = size.height * 0.4f
                    )
                )
                // Third cloud: base position + 1.5x offset
                drawCircle(
                    color = Color.White.copy(alpha = 0.45f),
                    radius = 25.dp.toPx(),
                    center = Offset(
                        x = size.width * 0.8f + cloudOffset * 1.5f,
                        y = size.height * 0.35f
                    )
                )
            }
            // Center text content
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CloudyWeatherCardWithMotionPreview() {
    MaterialTheme {
        CloudyWeatherCardWithMotion(
            temperature = 18.0,
            location = "Seattle, WA"
        )
    }
}
