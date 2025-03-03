package com.ylabz.basepro.feature.weather.ui.components.backgrounds

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Example implementation for a cloudy background animation:
@Composable
fun CloudyBackgroundAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 6000, easing = LinearEasing),
            RepeatMode.Reverse
        )
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFCFD8DC), Color(0xFFB0BEC5))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw a few clouds with horizontal motion
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = 30.dp.toPx(),
                center = Offset(size.width * 0.7f + cloudOffset, size.height * 0.3f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = 20.dp.toPx(),
                center = Offset(size.width * 0.6f + cloudOffset * 0.5f, size.height * 0.4f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.45f),
                radius = 25.dp.toPx(),
                center = Offset(size.width * 0.8f + cloudOffset * 1.5f, size.height * 0.35f)
            )
        }
    }
}

@Preview
@Composable
fun CloudyBackgroundAnimationPreview() {
    CloudyBackgroundAnimation()
}
