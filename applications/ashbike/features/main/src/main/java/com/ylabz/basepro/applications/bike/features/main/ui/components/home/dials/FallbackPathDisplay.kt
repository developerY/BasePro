package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme // For potential theme colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ylabz.basepro.core.model.location.GpsFix
import kotlin.math.max // Ensure kotlin.math.max is imported

// Copied and adapted from MapPathScreen.kt
private fun DrawScope.drawGrid(
    color: Color,
    rows: Int,
    cols: Int,
    strokeWidthBase: Float = 0.8f,
    strokeWidthThick: Float = 1.5f
) {
    val stepX = size.width / cols
    val stepY = size.height / rows
    repeat(cols - 1) { i ->
        val x = stepX * (i + 1)
        drawLine(
            color,
            Offset(x, 0f),
            Offset(x, size.height),
            strokeWidth = if ((i + 1) % 5 == 0) strokeWidthThick else strokeWidthBase
        )
    }
    repeat(rows - 1) { j ->
        val y = stepY * (j + 1)
        drawLine(
            color,
            Offset(0f, y),
            Offset(size.width, y),
            strokeWidth = if ((j + 1) % 5 == 0) strokeWidthThick else strokeWidthBase
        )
    }
}

private data class PathSegmentData(
    val startOffset: Offset,
    val endOffset: Offset,
    val speedKmh: Double
)

// Data class to hold all calculated values for drawing
private data class FallbackPathDrawingData(
    val segments: List<PathSegmentData>,
    val minSpeed: Double,
    val maxSpeed: Double
)

@Composable
fun FallbackPathDisplay(
    modifier: Modifier = Modifier,
    fixes: List<GpsFix>,
    backgroundColor: Color = Color(0xFFC6E2CC), // Similar to MapPathScreen start color
    gridColor: Color = Color.White.copy(alpha = 0.5f),
    slowColor: Color = Color(0xFFFFEB3B), // Yellow
    fastColor: Color = Color(0xFFBB190C), // Red
    textColor: Color = Color.Black
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val pathStrokeWidthPx = remember(density) { with(density) { 3.dp.toPx() } }
    val insetPx = remember(density) { with(density) { 16.dp.toPx() } }

    BoxWithConstraints(modifier = modifier) {
        // Remember all calculated data needed for drawing. Recalculates if fixes or dimensions change.
        val drawingData: FallbackPathDrawingData? = remember(fixes, maxWidth, maxHeight, density, insetPx) {
            if (fixes.size < 2) {
                null // Not enough data to draw a path
            } else {
                val minLat = fixes.minOf { it.lat }
                val maxLat = fixes.maxOf { it.lat }
                val minLng = fixes.minOf { it.lng }
                val maxLng = fixes.maxOf { it.lng }

                val latRange = (maxLat - minLat).takeIf { it > 0.00001 } ?: 0.001
                val lngRange = (maxLng - minLng).takeIf { it > 0.00001 } ?: 0.001

                val canvasViewWidthPx = with(density) { maxWidth.toPx() }
                val canvasViewHeightPx = with(density) { maxHeight.toPx() }
                
                val actualDrawableWidth = canvasViewWidthPx - 2 * insetPx
                val actualDrawableHeight = canvasViewHeightPx - 2 * insetPx

                val project: (Double, Double) -> Offset = { lat, lng ->
                    val x : Float= insetPx + (((lng - minLng) / lngRange) * actualDrawableWidth).toFloat()
                    val y : Float = insetPx + (((maxLat - lat) / latRange) * actualDrawableHeight).toFloat() // Y is inverted for canvas
                    Offset(
                        x.coerceIn(insetPx, canvasViewWidthPx - insetPx).toFloat(),
                        y.coerceIn(insetPx, canvasViewHeightPx - insetPx)
                    )
                }

                val segments = fixes.zipWithNext().map { (p0, p1) ->
                    val p0SpeedKmh = (p0.speed ?: 0f) * 3.6
                    PathSegmentData(
                        startOffset = project(p0.lat, p0.lng),
                        endOffset = project(p1.lat, p1.lng),
                        speedKmh = p0SpeedKmh
                    )
                }

                val minSpeed: Double
                val maxSpeed: Double
                if (segments.isEmpty()) { // Should not happen if fixes.size >= 2, but defensive
                    minSpeed = 0.0
                    maxSpeed = 1.0
                } else {
                    val speeds = segments.map { it.speedKmh }
                    minSpeed = speeds.minOrNull() ?: 0.0
                    maxSpeed = (speeds.maxOrNull() ?: 1.0).let { currentMax ->
                        if (currentMax <= minSpeed && segments.isNotEmpty()) minSpeed + 0.1 else currentMax
                    }
                }
                FallbackPathDrawingData(segments, minSpeed, maxSpeed)
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) { // Canvas fills the BoxWithConstraints
            drawRect(color = backgroundColor)
            drawGrid(color = gridColor, rows = 10, cols = 10)

            // Draw North Indicator ("N")
            val northTextLayout = textMeasurer.measure(
                "N",
                style = TextStyle(color = textColor.copy(alpha = 0.7f), fontSize = 14.sp)
            )
            drawText(
                textLayoutResult = northTextLayout,
                topLeft = Offset(this.size.width - insetPx - northTextLayout.size.width, insetPx / 2)
            )

            if (drawingData != null) {
                val (pathSegments, minSpeed, maxSpeed) = drawingData
                if (pathSegments.isNotEmpty()) { // segments list inside drawingData might be empty if all fixes were identical
                    pathSegments.forEach { segment ->
                        val speedFraction = if (maxSpeed - minSpeed > 0.001) {
                            ((segment.speedKmh - minSpeed) / (maxSpeed - minSpeed)).toFloat().coerceIn(0f, 1f)
                        } else {
                            0f // Default to slow color if no speed variation
                        }
                        val color = lerp(slowColor, fastColor, speedFraction)
                        drawLine(
                            color = color,
                            start = segment.startOffset,
                            end = segment.endOffset,
                            strokeWidth = pathStrokeWidthPx,
                            cap = StrokeCap.Round
                        )
                    }
                }
            } else {
                // fixes.size < 2 or other issue leading to null drawingData
                val textLayoutResult = textMeasurer.measure(
                    text = "No path data to display",
                    style = TextStyle(color = textColor, fontSize = 16.sp, textAlign = TextAlign.Center),
                    constraints = androidx.compose.ui.unit.Constraints(maxWidth = (size.width - 2 * insetPx).toInt())
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        x = (size.width - textLayoutResult.size.width) / 2,
                        y = (size.height - textLayoutResult.size.height) / 2
                    )
                )
            }
        }
    }
}
