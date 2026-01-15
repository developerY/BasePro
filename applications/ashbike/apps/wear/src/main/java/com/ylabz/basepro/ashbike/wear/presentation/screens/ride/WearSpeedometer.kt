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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun WearSpeedometer(
    currentSpeed: Float,
    maxSpeed: Float = 40f, // Adjust based on bike type (40 for city, 60 for road)
    modifier: Modifier = Modifier,
    indicatorColor: Color = Color(0xFF4FC3F7), // Light Blue
    trackColor: Color = Color.DarkGray.copy(alpha = 0.3f)
) {
    Box(modifier = modifier.aspectRatio(1f).padding(10.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2, h / 2)
            // Leave room for stroke width
            val strokeWidth = 12.dp.toPx()
            val radius = (min(w, h) - strokeWidth) / 2

            // Arc Logic: 240-degree arc starting from bottom-left
            // Start: 150 degrees (7 o'clock)
            // Sweep: 240 degrees (Ending at 5 o'clock)
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

            // 2. Draw Active Speed Arc
            val speedFraction = (currentSpeed / maxSpeed).coerceIn(0f, 1f)
            val activeSweep = sweepAngle * speedFraction

            drawArc(
                color = indicatorColor,
                startAngle = startAngle,
                sweepAngle = activeSweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 3. Draw Indicator Dot (Optional Tip of the arc)
            val currentAngleRad = Math.toRadians((startAngle + activeSweep).toDouble())
            val dotX = center.x + radius * Math.cos(currentAngleRad).toFloat()
            val dotY = center.y + radius * Math.sin(currentAngleRad).toFloat()

            drawCircle(
                color = Color.White,
                radius = strokeWidth / 1.5f,
                center = Offset(dotX, dotY)
            )
        }
    }
}