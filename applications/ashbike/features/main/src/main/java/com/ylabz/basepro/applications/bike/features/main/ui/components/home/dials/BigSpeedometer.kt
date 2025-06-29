package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.ui.theme.AshBikeTheme
import com.ylabz.basepro.core.ui.theme.CustomColors
import com.ylabz.basepro.core.ui.theme.LocalCustomColors
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Calculates the color for a given speed fraction based on the theme's custom speed colors.
 * This ensures the logic for the text color matches the gradient brush.
 */
private fun calculateColorForSpeed(fraction: Float, colors: CustomColors): Color {
    return when {
        fraction <= 0.5f -> lerp(colors.speedSlow, colors.speedMedium, fraction * 2)
        else -> lerp(colors.speedMedium, colors.speedFast, (fraction - 0.5f) * 2)
    }
}

@Composable
fun BigSpeedometer(
    currentSpeed: Float,
    modifier: Modifier = Modifier,
    maxSpeed: Float = 60f
) {
    val startAngle = 135f
    val sweepAngle = 270f
    val speedFraction = (currentSpeed / maxSpeed).coerceIn(0f, 1f)

    val overshootEasing = Easing { fraction ->
        val tension = 2.0f
        val x = fraction - 1.0f
        x * x * ((tension + 1) * x + tension) + 1.0f
    }

    val targetNeedleAngle = startAngle + sweepAngle * speedFraction
    val animatedNeedleAngle by animateFloatAsState(
        targetValue = targetNeedleAngle,
        label = "NeedleAngle",
        animationSpec = tween(durationMillis = 800, easing = overshootEasing)
    )

    // === RESOLVE ALL COLORS IN THE COMPOSABLE SCOPE ===
    val customColors = LocalCustomColors.current
    val progressBrush = Brush.sweepGradient(
        colors = listOf(customColors.speedSlow, customColors.speedMedium, customColors.speedFast)
    )
    val backgroundArcColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val needleColor = MaterialTheme.colorScheme.onSurface
    val centerCapColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val tickColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)

    // This calculation ensures the text color always matches the arc color at the needle's position.
    val animatedSpeedFraction = ((animatedNeedleAngle - startAngle) / sweepAngle).coerceIn(0f, 1f)
    val speedTextColor = calculateColorForSpeed(animatedSpeedFraction, customColors)
    val speedUnitColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)


    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2.3f
        val arcStrokeWidth = size.minDimension / 6f

        // 1. Background arc
        drawArc(
            color = backgroundArcColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = arcStrokeWidth, cap = StrokeCap.Round)
        )

        // 2. Progress arc
        val arcSweep = (animatedNeedleAngle - startAngle).coerceAtLeast(0f)
        drawArc(
            brush = progressBrush,
            startAngle = startAngle,
            sweepAngle = arcSweep,
            useCenter = false,
            style = Stroke(width = arcStrokeWidth, cap = StrokeCap.Round)
        )

        // 3. Tick lines & labels
        val tickCount = (maxSpeed / 10f).toInt()
        val tickAngleStep = sweepAngle / tickCount
        val tickPaint = Paint().apply {
            color = tickColor.toArgb()
            textSize = size.minDimension / 18f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        for (i in 0..tickCount) {
            val angle = startAngle + i * tickAngleStep
            val angleRad = Math.toRadians(angle.toDouble())
            val labelRadius = radius - (arcStrokeWidth / 2) - (tickPaint.textSize / 2) - 10f
            val lx = center.x + cos(angleRad).toFloat() * labelRadius
            val ly = center.y + sin(angleRad).toFloat() * labelRadius

            drawContext.canvas.nativeCanvas.drawText(
                (i * 10).toString(),
                lx,
                ly + tickPaint.textSize / 3f,
                tickPaint
            )
        }


        // 4. Needle
        val needleAngleRad = Math.toRadians(animatedNeedleAngle.toDouble())
        val needleLength = radius - (arcStrokeWidth / 2)
        val needleStart = center - Offset(
            x = cos(needleAngleRad).toFloat() * (size.minDimension / 15f),
            y = sin(needleAngleRad).toFloat() * (size.minDimension / 15f)
        )
        val needleEnd = Offset(
            x = center.x + cos(needleAngleRad).toFloat() * needleLength,
            y = center.y + sin(needleAngleRad).toFloat() * needleLength
        )
        drawLine(
            color = needleColor,
            start = needleStart,
            end = needleEnd,
            strokeWidth = size.minDimension / 60f,
            cap = StrokeCap.Round
        )

        // 5. Center cap
        drawCircle(
            color = centerCapColor,
            radius = size.minDimension / 15f,
            center = center
        )
        drawCircle(
            color = needleColor,
            radius = size.minDimension / 30f,
            center = center
        )

        // 6. Speed Text
        drawContext.canvas.nativeCanvas.apply {
            val speedNumberText = currentSpeed.roundToInt().toString()
            val speedUnitText = "km/h"

            val numberTextPaint = Paint().apply {
                color = speedTextColor.toArgb()
                textSize = size.minDimension / 3f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
                typeface = Typeface.create("", Typeface.BOLD)
            }
            val unitTextPaint = Paint().apply {
                color = speedUnitColor.toArgb()
                textSize = size.minDimension / 12f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            val textY = center.y + (numberTextPaint.textSize / 4f)
            drawText(speedNumberText, center.x, textY, numberTextPaint)
            drawText(speedUnitText, center.x, textY + unitTextPaint.textSize, unitTextPaint)
        }
    }
}


@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun BigSpeedometerPreview() {
    AshBikeTheme(theme = "Light") {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BigSpeedometer(modifier = Modifier.size(250.dp), currentSpeed = 35f)
        }
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun BigSpeedometerDarkPreview() {
    AshBikeTheme(theme = "Dark") {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BigSpeedometer(modifier = Modifier.size(250.dp), currentSpeed = 65f)
        }
    }
}