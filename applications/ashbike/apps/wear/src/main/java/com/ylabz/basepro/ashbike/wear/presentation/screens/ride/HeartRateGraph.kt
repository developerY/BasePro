package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun HeartRateGraph(
    dataPoints: List<Int>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFFFF5252) // Reddish color
) {
    if (dataPoints.isEmpty()) return

    // Pre-calculate min/max for scaling
    val minHr = remember(dataPoints) { dataPoints.minOrNull() ?: 0 }
    val maxHr = remember(dataPoints) { dataPoints.maxOrNull() ?: 200 }
    val range = (maxHr - minHr).coerceAtLeast(1) // Avoid divide by zero

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp) // Fixed height for the graph
    ) {
        val width = size.width
        val height = size.height

        // Calculate the X step (distance between points)
        val stepX = width / (dataPoints.size - 1).coerceAtLeast(1)

        val path = Path().apply {
            dataPoints.forEachIndexed { index, hr ->
                val x = index * stepX
                // Normalize HR to height (invert Y because canvas 0 is top)
                val normalizedY = 1f - ((hr - minHr) / range.toFloat())
                val y = normalizedY * height

                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }

        // Draw the line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
        )

        // Optional: Add a gradient fill below the line
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent)
            )
        )
    }
}