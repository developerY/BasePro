package com.ylabz.basepro.feature.weather.ui.components.wind

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WindDirectionDial(degree: Float, modifier: Modifier = Modifier.size(150.dp)) {
    // Create an infinite transition for a subtle wiggle effect.
    val infiniteTransition = rememberInfiniteTransition()
    val wiggle by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val effectiveDegree = degree + wiggle

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Draw the dial circle.
            drawCircle(
                color = Color.LightGray,
                radius = radius,
                center = center,
                style = Stroke(width = 4f)
            )

            // Draw degree markers every 30°.
            for (i in 0 until 360 step 30) {
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
                    strokeWidth = 3f
                )
            }

            // Draw the wind arrow.
            val arrowLength = radius * 0.7f
            val arrowAngleRad = Math.toRadians((effectiveDegree - 90).toDouble())
            val arrowEnd = Offset(
                center.x + (arrowLength * cos(arrowAngleRad)).toFloat(),
                center.y + (arrowLength * sin(arrowAngleRad)).toFloat()
            )
            // Create an arrow path with a triangular tip.
            val arrowPath = Path().apply {
                moveTo(center.x, center.y)
                lineTo(
                    center.x + (arrowLength * 0.1f * cos(arrowAngleRad + Math.PI / 2)).toFloat(),
                    center.y + (arrowLength * 0.1f * sin(arrowAngleRad + Math.PI / 2)).toFloat()
                )
                lineTo(arrowEnd.x, arrowEnd.y)
                lineTo(
                    center.x + (arrowLength * 0.1f * cos(arrowAngleRad - Math.PI / 2)).toFloat(),
                    center.y + (arrowLength * 0.1f * sin(arrowAngleRad - Math.PI / 2)).toFloat()
                )
                close()
            }
            drawPath(
                path = arrowPath,
                color = Color.Red
            )
        }
    }
}
/*
@Preview
@Composable
fun WindDirectionDialPreview() {
    WindDirectionDial(degree = 45f)
}
*/