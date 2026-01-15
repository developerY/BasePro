package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun WearSpeedometer(
    currentSpeed: Float,
    maxSpeed: Float = 40f,
    modifier: Modifier = Modifier,
    // FIX: Use a Gradient Brush instead of a single color
    indicatorBrush: Brush = Brush.sweepGradient(
        colors = listOf(
            Color(0xFFFFFF00), // Yellow
            Color(0xFFFF9800), // Orange
            Color(0xFFFF5252)  // Red
        )
    ),
    trackColor: Color = Color.DarkGray.copy(alpha = 0.3f)
) {
    Box(modifier = modifier.aspectRatio(1f).padding(10.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2, h / 2)
            val strokeWidth = 12.dp.toPx()
            val radius = (min(w, h) - strokeWidth) / 2

            // Start at 7 o'clock (150deg), End at 5 o'clock
            val startAngle = 150f
            val sweepAngle = 240f

            // 1. Draw Background Track
            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 2. Draw Active Gradient Arc
            val speedFraction = (currentSpeed / maxSpeed).coerceIn(0f, 1f)
            val activeSweep = sweepAngle * speedFraction

            if (currentSpeed > 0) {
                drawArc(
                    brush = indicatorBrush, // <--- Use Brush here
                    startAngle = startAngle,
                    sweepAngle = activeSweep,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // 3. Draw Indicator Dot
            // Math to find the tip of the arc
            val currentAngleRad = Math.toRadians((startAngle + activeSweep).toDouble())
            val dotX = center.x + radius * cos(currentAngleRad).toFloat()
            val dotY = center.y + radius * sin(currentAngleRad).toFloat()

            drawCircle(
                color = Color.White,
                radius = strokeWidth / 1.5f,
                center = Offset(dotX, dotY)
            )
        }
    }
}