package com.ylabz.basepro.feature.weather.ui.components.combine

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.sin

/**
 * A wind dial that shows the wind direction (arrow) and wind speed in the center.
 */
@Composable
fun WindDirectionDialWithSpeed(
    degree: Int,
    speed: Double,
    modifier: Modifier = Modifier
) {
    // 1) Create an infinite transition for the wiggle
    val infiniteTransition = rememberInfiniteTransition()
    val wiggleOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // 2) Combine the base degree with the wiggle offset
    val arrowAngle = degree + wiggleOffset

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

            // Markers every 45°
            for (i in 0..360 step 45) {
                val angleRad = Math.toRadians((i - 90).toDouble())
                val markerStart = Offset(
                    center.x + (radius * 0.85f * cos(angleRad)).toFloat(),
                    center.y + (radius * 0.85f * sin(angleRad)).toFloat()
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

            // 3) Use arrowAngle instead of degree
            val arrowLength = radius * 0.6f
            val arrowAngleRad = Math.toRadians((arrowAngle - 90).toDouble())

            val tipX = center.x + (arrowLength * cos(arrowAngleRad)).toFloat()
            val tipY = center.y + (arrowLength * sin(arrowAngleRad)).toFloat()

            // Arrow shaft
            drawLine(
                color = Color.Red,
                start = center,
                end = Offset(tipX, tipY),
                strokeWidth = 3f
            )

            // Simple arrow head
            val headSize = 6f
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
                strokeWidth = 3f
            )
            drawLine(
                color = Color.Red,
                start = Offset(tipX, tipY),
                end = Offset(rightX, rightY),
                strokeWidth = 3f
            )
        }

        // Wind speed in the center
        Text(
            text = "${speed} m/s",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Black
        )
    }
}


@Preview
@Composable
fun WindDirectionDialWithSpeedPreview() {
    val degree = 180
    val speed = 10.2
    WindDirectionDialWithSpeed(degree = degree, speed = speed)
}
