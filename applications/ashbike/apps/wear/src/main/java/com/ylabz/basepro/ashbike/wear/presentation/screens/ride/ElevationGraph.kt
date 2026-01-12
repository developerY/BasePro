package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp

@Composable
fun ElevationGraph(
    elevations: List<Float>,
    modifier: Modifier = Modifier,
    graphColor: Color = Color(0xFF4FC3F7) // Light Cyan
) {
    if (elevations.isEmpty()) return

    // 1. Calculate Min/Max to scale the peaks
    val minAlt = remember(elevations) { elevations.minOrNull() ?: 0f }
    val maxAlt = remember(elevations) { elevations.maxOrNull() ?: 100f }
    val range = (maxAlt - minAlt).coerceAtLeast(1f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        val width = size.width
        val height = size.height
        val stepX = width / (elevations.size - 1).coerceAtLeast(1)

        // 2. Create the Mountain Path
        val path = Path().apply {
            // Start at bottom-left
            moveTo(0f, height)

            elevations.forEachIndexed { index, alt ->
                val x = index * stepX
                // Normalize Y: Higher altitude = Smaller Y (closer to top)
                val normalizedY = 1f - ((alt - minAlt) / range)
                val y = normalizedY * height
                lineTo(x, y)
            }

            // Close the shape (Bottom-Right -> Bottom-Left)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        // 3. Draw with a Vertical Gradient (Fades out at bottom)
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(
                    graphColor.copy(alpha = 0.8f), // Solid at peak
                    graphColor.copy(alpha = 0.1f)  // Transparent at base
                ),
                startY = 0f,
                endY = height
            ),
            style = Fill
        )
    }
}