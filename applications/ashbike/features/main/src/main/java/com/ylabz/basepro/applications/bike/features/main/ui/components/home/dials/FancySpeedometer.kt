package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


@Composable
fun FancySpeedometer(
    currentSpeed: Float,
    maxSpeed: Float = 60f,
    modifier: Modifier = Modifier.size(250.dp)
) {
    // Arc angles
    val startAngle = 135f
    val sweepAngle = 270f
    val endAngle = startAngle + sweepAngle

    // Convert speed to fraction [0..1]
    val speedFraction = (currentSpeed / maxSpeed).coerceIn(0f, 1f)

    // Example overshoot easing
    val overshootEasing: Easing = Easing { fraction ->
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
            easing = overshootEasing
        )
    )

    val progressBrush = Brush.sweepGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFFFF5722),  // dark green
            0.2f to Color(0xFFF44336),
            0.3f to Color(0xFF1E561F),
            0.4f to Color(0xFF349439),
            0.5f to Color(0xFF68B739),
            0.6f to Color(0xFFA6C476),
            0.7f to Color(0xFFCFFF22),
            0.8f to Color(0xFFFFE607),
            0.9f to Color(0xFFFFB13B),
            1.0f to Color(0xFFFF5722)// Color(0xFFFF9800)
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Use 'size.minDimension' to get the smaller dimension if needed
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2.2f

        // 1) Background arc (light gray)
        drawArc(
            color = Color.LightGray,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = 70f, cap = StrokeCap.Round)
        )

        // 2) Progress arc (green → red)
        // Arc is from startAngle to animatedNeedleAngle
        val arcSweep = (animatedNeedleAngle - startAngle).coerceAtLeast(0f)
        drawArc(
            brush = progressBrush,
            startAngle = startAngle,
            sweepAngle = arcSweep,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = 70f, cap = StrokeCap.Round)
        )

        // 3) Tick lines & labels
        val tickCount = ((maxSpeed / 10).toInt()).coerceAtLeast(1)
        val tickAngleStep = sweepAngle / tickCount
        for (i in 0..tickCount) {
            val tickSpeed = i * 10
            val angle = startAngle + i * tickAngleStep
            val angleRad = Math.toRadians(angle.toDouble())

            // Tick lines
            val outerRadius = radius
            val innerRadius = radius - 50
            val sx = center.x + cos(angleRad).toFloat() * outerRadius
            val sy = center.y + sin(angleRad).toFloat() * outerRadius
            val ex = center.x + cos(angleRad).toFloat() * innerRadius
            val ey = center.y + sin(angleRad).toFloat() * innerRadius
            drawLine(
                color = Color.DarkGray,
                start = Offset(sx, sy),
                end = Offset(ex, ey),
                strokeWidth = 3f
            )

            // Speed labels
            val labelRadius = radius - 45
            val lx = center.x + cos(angleRad).toFloat() * labelRadius
            val ly = center.y + sin(angleRad).toFloat() * labelRadius

            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 32f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.create("", android.graphics.Typeface.BOLD)
                }
                drawText(tickSpeed.toString(), lx, ly + 10f, paint)
            }
        }

        // 4) Needle
        val needleAngleRad = Math.toRadians(animatedNeedleAngle.toDouble())
        val needleLength = radius - 35
        val needleEnd = Offset(
            x = center.x + cos(needleAngleRad).toFloat() * needleLength,
            y = center.y + sin(needleAngleRad).toFloat() * needleLength
        )
        drawLine(
            color = Color.Red,
            start = center,
            end = needleEnd,
            strokeWidth = 34f,
            cap = StrokeCap.Round
        )

        // 5) Center cap
        drawCircle(
            color = Color.Red,
            radius = 12f,
            center = center
        )

        // 6) Speed text in center
            val speedText = "${currentSpeed.roundToInt()}"
            drawContext.canvas.nativeCanvas.apply {
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 250f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.create("", android.graphics.Typeface.BOLD)
                }
                drawText(
                    speedText,
                    center.x,
                    center.y + 20f,
                    textPaint
                )
            }
        }

}

private fun Float.toRadians() = this * (Math.PI / 180f).toFloat()

@Preview(showBackground = true)
@Composable
fun FancySpeedometerPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        FancySpeedometer(currentSpeed = 59f)
    }
}
