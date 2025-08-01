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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.ui.theme.AshBikeTheme
import com.ylabz.basepro.core.ui.theme.SpeedometerGreen
import com.ylabz.basepro.core.ui.theme.SpeedometerRed
import com.ylabz.basepro.core.ui.theme.SpeedometerYellow
import com.ylabz.basepro.core.ui.theme.getColorForSpeed
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun FancySpeedometer(
    currentSpeed: Float,
    maxSpeed: Float = 60f,
    modifier: Modifier = Modifier.size(250.dp),
    contentColor: Color
) {
    // Arc angles
    val startAngle = 135f
    val sweepAngle = 270f

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
        ), label = ""
    )

    val progressBrush = Brush.sweepGradient(
        colors = listOf(SpeedometerRed, SpeedometerGreen, SpeedometerYellow, SpeedometerRed)
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2.3f

        // 1) Background arc
        drawArc(
            color = contentColor.copy(alpha = 0.1f),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = 70f, cap = StrokeCap.Round)
        )

        // 2) Progress arc
        val arcSweep = (animatedNeedleAngle - startAngle).coerceAtLeast(0f)
        drawArc(
            brush = progressBrush,
            startAngle = startAngle,
            sweepAngle = arcSweep,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2f, radius * 2f),
            style = Stroke(width = 110f, cap = StrokeCap.Round)
        )

        // 3) Tick lines & labels
        val tickCount = ((maxSpeed / 10).toInt()).coerceAtLeast(1)
        val tickAngleStep = sweepAngle / tickCount
        for (i in 0..tickCount) {
            val tickSpeed = i * 10
            val angle = startAngle + i * tickAngleStep
            val angleRad = Math.toRadians(angle.toDouble())

            val outerRadius = radius
            val innerRadius = radius - 50
            val sx = center.x + cos(angleRad).toFloat() * outerRadius
            val sy = center.y + sin(angleRad).toFloat() * outerRadius
            val ex = center.x + cos(angleRad).toFloat() * innerRadius
            val ey = center.y + sin(angleRad).toFloat() * innerRadius
            drawLine(
                color = contentColor.copy(alpha = 0.5f),
                start = Offset(sx, sy),
                end = Offset(ex, ey),
                strokeWidth = 3f
            )

            val labelRadius = radius - 45
            val lx = center.x + cos(angleRad).toFloat() * labelRadius
            val ly = center.y + sin(angleRad).toFloat() * labelRadius

            drawContext.canvas.nativeCanvas.apply {
                val isTickActive = tickSpeed <= currentSpeed
                val labelColor = if (isTickActive) {
                    contentColor.toArgb()
                } else {
                    contentColor.copy(alpha = 0.7f).toArgb()
                }

                val paint = Paint().apply {
                    color = labelColor
                    textSize = 72f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                    typeface = Typeface.create("", Typeface.BOLD)
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
            color = contentColor,
            start = center,
            end = needleEnd,
            strokeWidth = 34f,
            cap = StrokeCap.Round
        )

        // 5) Center cap
        drawCircle(
            color = contentColor.copy(alpha = 0.4f),
            radius = 27f,
            center = center
        )

        // G) Speed Text (Number + Unit)
        drawContext.canvas.nativeCanvas.apply {
            val speedNumberText = currentSpeed.roundToInt().toString()
            val speedUnitText = "km/h"

            val dynamicColor = getColorForSpeed(currentSpeed, maxSpeed).toArgb()

            val outlinePaint = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 380f
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
                typeface = Typeface.create("", Typeface.BOLD)
                style = Paint.Style.STROKE
                strokeWidth = 10f
                strokeJoin = Paint.Join.ROUND
            }

            val numberTextPaint = Paint().apply {
                color = dynamicColor
                textSize = 380f
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
                typeface = Typeface.create("", Typeface.BOLD)
            }

            val unitTextPaint = Paint().apply {
                color = contentColor.toArgb()
                textSize = 40f
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
                typeface = Typeface.create("", Typeface.NORMAL)
            }

            val numberTextWidth = numberTextPaint.measureText(speedNumberText)
            val totalTextWidth = numberTextWidth

            val startX = center.x - (totalTextWidth / 2f)
            val textY = center.y + (numberTextPaint.textSize / 4f)

            drawText(
                speedNumberText,
                startX,
                textY,
                outlinePaint
            )

            drawText(
                speedNumberText,
                startX,
                textY,
                numberTextPaint
            )

            drawText(
                speedUnitText,
                startX + numberTextWidth,
                textY,
                unitTextPaint
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun FancySpeedometerPreview() {
    AshBikeTheme(theme = "Light") {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            FancySpeedometer(
                modifier = Modifier.size(250.dp),
                currentSpeed = 35f,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun FancySpeedometerDarkPreview() {
    AshBikeTheme(theme = "Dark") {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            FancySpeedometer(
                modifier = Modifier.size(250.dp),
                currentSpeed = 55f,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
