package com.ylabz.basepro.applications.bike.ui.components.unused

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import kotlin.math.*

private fun Float.toRadians() = this * (Math.PI / 180f).toFloat()

@Composable
fun Speedometer(
    currentSpeed: Float,      // Current speed
    maxSpeed: Float = 60f,    // Max speed for the gauge
    modifier: Modifier = Modifier.size(200.dp)
) {
    // Map currentSpeed to an angle. We define our arc from 135° to 405° (a 270° sweep).
    // That’s a typical “half circle + some” gauge shape. You can adjust to taste.
    val startAngle = 135f
    val sweepAngle = 270f
    val endAngle = startAngle + sweepAngle
    val speedFraction = (currentSpeed / maxSpeed).coerceIn(0f, 1f)
    val needleAngle = startAngle + (sweepAngle * speedFraction)

    // Animate the needle for a smooth transition
    val animatedSpeed by animateFloatAsState(
        targetValue = needleAngle,
        animationSpec = tween(durationMillis = 700, easing = LinearEasing)
    )

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val gaugeSize = with(density){min(maxWidth, maxHeight).toPx()}
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Center and radius
            val center = Offset(size.width / 2, size.height / 2)
            val radius = gaugeSize / 2.2f

            // 1) Draw the background arc
            drawArc(
                color = Color.LightGray,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 20f, cap = StrokeCap.Round)
            )

            // 2) Draw the “progress” arc in a gradient
            val progressBrush = Brush.sweepGradient(
                listOf(
                    Color(0xFF1976D2),
                    Color(0xFF64B5F6),
                    Color(0xFF1976D2)
                )
            )
            drawArc(
                brush = progressBrush,
                startAngle = startAngle,
                sweepAngle = (animatedSpeed - startAngle).coerceAtLeast(0f),
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 20f, cap = StrokeCap.Round)
            )

            // 3) Draw small tick marks
            val tickCount = 10
            val tickAngleStep = sweepAngle / tickCount
            for (i in 0..tickCount) {
                val angle = startAngle + i * tickAngleStep
                val angleRad = angle.toRadians()
                val outerRadius = radius
                val innerRadius = radius - 20
                val sx = center.x + cos(angleRad) * outerRadius
                val sy = center.y + sin(angleRad) * outerRadius
                val ex = center.x + cos(angleRad) * innerRadius
                val ey = center.y + sin(angleRad) * innerRadius

                drawLine(
                    color = Color.DarkGray,
                    start = Offset(sx, sy),
                    end = Offset(ex, ey),
                    strokeWidth = 3f
                )
            }

            // 4) Draw the needle
            val needleLength = radius - 30
            val needleAngleRad = animatedSpeed.toRadians()
            val needleEnd = Offset(
                x = center.x + cos(needleAngleRad) * needleLength,
                y = center.y + sin(needleAngleRad) * needleLength
            )
            drawLine(
                color = Color.Red,
                start = center,
                end = needleEnd,
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )

            // 5) Draw the center cap
            drawCircle(
                color = Color.Red,
                radius = 10f,
                center = center
            )

            // 6) Draw speed text in the center
            val speedText = "${currentSpeed.roundToInt()} km/h"
            drawIntoCanvas { canvas ->
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 50f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.create("", android.graphics.Typeface.BOLD)
                }
                canvas.nativeCanvas.drawText(
                    speedText,
                    center.x,
                    center.y + 15f,  // vertical offset to center text
                    textPaint
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpeedometerPreview() {
    Speedometer(currentSpeed = 30f, maxSpeed = 100f)
}
