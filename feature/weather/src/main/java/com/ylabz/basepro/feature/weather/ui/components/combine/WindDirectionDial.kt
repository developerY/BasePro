package com.ylabz.basepro.feature.weather.ui.components.combine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WindDirectionDialUni(
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

            // Red arrow
            val arrowLength = radius * 0.6f
            val arrowAngleRad = Math.toRadians((degree - 90).toDouble())

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
    }
}
