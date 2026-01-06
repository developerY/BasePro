package com.ylabz.basepro.ashbike.wear.presentation.components

// import androidx.wear.tooling.preview.devices.WearDevices
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun WearSpeedometer(
    currentSpeed: Float,
    maxSpeed: Float = 45f, // Lower max speed for bikes often makes the needle move more :)
    modifier: Modifier = Modifier
) {
    // Colors from Material 3 Theme
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surfaceContainer = MaterialTheme.colorScheme.surfaceContainer

    // Arc settings - customized for Wear (Top 240 degrees visible)
    val startAngle = 150f
    val sweepAngle = 240f

    val speedFraction = (currentSpeed / maxSpeed).coerceIn(0f, 1f)

    // Smooth overshoot animation for the needle
    val overshootEasing = Easing { fraction ->
        val tension = 2.0f
        val x = fraction - 1.0f
        x * x * ((tension + 1) * x + tension) + 1.0f
    }

    val targetNeedleAngle = startAngle + sweepAngle * speedFraction
    val animatedNeedleAngle by animateFloatAsState(
        targetValue = targetNeedleAngle,
        animationSpec = tween(durationMillis = 800, easing = overshootEasing),
        label = "NeedleAnimation"
    )

    // Gradient logic: Green -> Yellow -> Red (Performance indicators)
    val progressBrush = Brush.sweepGradient(
        colors = listOf(Color.Green, Color.Yellow, Color.Red)
    )

    Canvas(modifier = modifier.fillMaxSize().padding(8.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        // Ensure the arc fits within the screen bounds minus padding
        val radius = size.minDimension / 2f - 20f
        val arcThickness = 25f

        // 1. Background Arc (Track)
        drawArc(
            color = surfaceContainer,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = arcThickness, cap = StrokeCap.Round)
        )

        // 2. Active Progress Arc
        val arcSweep = (animatedNeedleAngle - startAngle).coerceAtLeast(0f)
        drawArc(
            brush = progressBrush,
            startAngle = startAngle,
            sweepAngle = arcSweep,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = arcThickness, cap = StrokeCap.Round)
        )

        // 3. The Needle Indicator (Small circle on the track)
        val needleRad = Math.toRadians(animatedNeedleAngle.toDouble())
        val knobCenter = Offset(
            x = center.x + cos(needleRad).toFloat() * radius,
            y = center.y + sin(needleRad).toFloat() * radius
        )

        // Draw knob glow
        drawCircle(
            color = primaryColor.copy(alpha = 0.3f),
            radius = 20f,
            center = knobCenter
        )
        // Draw solid knob
        drawCircle(
            color = Color.White,
            radius = 10f,
            center = knobCenter
        )

        // 4. Center Text (Speed Value) - Using Native Canvas for exact text centering
        drawContext.canvas.nativeCanvas.apply {
            val speedText = currentSpeed.roundToInt().toString()
            val labelText = "km/h"

            val textPaint = Paint().apply {
                color = onSurface.toArgb()
                textSize = 100f // Big font for Wear
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
                typeface = Typeface.DEFAULT_BOLD
            }

            val labelPaint = Paint().apply {
                color = onSurface.copy(alpha = 0.6f).toArgb()
                textSize = 30f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            // Draw Speed number
            drawText(speedText, center.x, center.y + 20f, textPaint)

            // Draw "km/h" label below
            drawText(labelText, center.x, center.y + 60f, labelPaint)
        }
    }
}

@Preview(showSystemUi = true, device = "id:wearos_xl_round") //device = WearDevices.SMALL_ROUND
@Composable
fun SpeedometerPreview() {
    Box(modifier = Modifier.fillMaxSize().padding(10.dp), contentAlignment = Alignment.Center) {
        WearSpeedometer(currentSpeed = 24.5f)
    }
}