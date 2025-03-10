package com.ylabz.basepro.feature.bike.ui.components.home.dials

import android.R.attr.maxHeight
import android.R.attr.maxWidth
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.*
import androidx.compose.animation.core.Easing
import androidx.compose.ui.graphics.drawscope.Stroke




@Composable
fun FancySpeedometer(
    currentSpeed: Float,      // Current speed
    maxSpeed: Float = 60f,    // Max speed for the gauge
    modifier: Modifier = Modifier.size(250.dp)
) {
    // Arc angles
    val startAngle = 135f
    val sweepAngle = 270f
    val endAngle = startAngle + sweepAngle

    // Convert speed to fraction [0..1]
    val speedFraction = (currentSpeed / maxSpeed).coerceIn(0f, 1f)

    val OvershootEasing: Easing = Easing { fraction ->
        // A simple overshoot function; adjust 'tension' to control the overshoot amount.
        val tension = 2.0f
        val x = fraction - 1.0f
        x * x * ((tension + 1) * x + tension) + 1.0f
    }

    // Animate the needle with an overshoot effect for a gentle bounce
    val targetNeedleAngle = startAngle + sweepAngle * speedFraction
    val animatedNeedleAngle by animateFloatAsState(
        targetValue = targetNeedleAngle,
        animationSpec = tween(
            durationMillis = 800,
            easing = OvershootEasing
        )
    )

    //BoxWithConstraints(modifier = modifier) {
        // Convert the smaller dimension from dp to pixels
        // Start drawing
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Use 'size.minDimension' to get the smaller dimension if needed
            // Use 'size.minDimension' to get the smaller dimension if needed
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2.2f
            // 1) Draw the background arc (light gray)
            drawArc(
                color = Color.LightGray,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 25f, cap = StrokeCap.Round)
            )

            // 2) Draw the “progress” arc in a multi-step gradient
            val progressBrush = Brush.sweepGradient(
                listOf(
                    Color(0xFF4CAF50), // green
                    Color(0xFFFFC107), // yellow
                    Color(0xFFFF5722), // orange
                    Color(0xFFF44336), // red
                    Color(0xFF4CAF50)  // back to green to complete the sweep
                )
            )
            val arcSweep = (animatedNeedleAngle - startAngle).coerceAtLeast(0f)
            drawArc(
                brush = progressBrush,
                startAngle = startAngle,
                sweepAngle = arcSweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 25f, cap = StrokeCap.Round)
            )

            // 3) Major tick lines + labels
            val tickCount = ((maxSpeed / 10).toInt()).coerceAtLeast(1)
            val tickAngleStep = sweepAngle / tickCount
            val labelStyle = TextStyle(
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            for (i in 0..tickCount) {
                val tickSpeed = i * 10
                val angle = startAngle + i * tickAngleStep
                val angleRad = angle.toRadians()

                // Tick lines
                val outerRadius = radius
                val innerRadius = radius - 25
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

                // Label position
                val labelRadius = radius - 45
                val lx = center.x + cos(angleRad) * labelRadius
                val ly = center.y + sin(angleRad) * labelRadius

                // We can draw text using drawContext.canvas.nativeCanvas
                // Or we can measure/draw text. For simplicity, we use a quick approach:
                drawContext.canvas.nativeCanvas.apply {
                    val labelText = "$tickSpeed"
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 32f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                        typeface = android.graphics.Typeface.create("", android.graphics.Typeface.BOLD)
                    }
                    // Slight vertical offset to center text
                    drawText(labelText, lx, ly + 10f, paint)
                }
            }

            // 4) Needle
            val needleAngleRad = animatedNeedleAngle.toRadians()
            val needleLength = radius - 35
            val needleEnd = Offset(
                x = center.x + cos(needleAngleRad) * needleLength,
                y = center.y + sin(needleAngleRad) * needleLength
            )
            drawLine(
                color = Color.Red,
                start = center,
                end = needleEnd,
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )

            // 5) Center cap
            drawCircle(
                color = Color.Red,
                radius = 12f,
                center = center
            )

            // 6) Speed text in center
            val speedText = "${currentSpeed.roundToInt()} km/h"
            drawContext.canvas.nativeCanvas.apply {
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 50f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.create("", android.graphics.Typeface.BOLD)
                }
                drawText(
                    speedText,
                    center.x,
                    center.y + 20f, // shift downward to center text vertically
                    textPaint
                )
            }
        }
    //}
}

private fun Float.toRadians() = this * (Math.PI / 180f).toFloat()

@Preview(showBackground = true)
@Composable
fun FancySpeedometerPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        FancySpeedometer(currentSpeed = 45f)
    }
}
