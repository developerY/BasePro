package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import kotlin.math.max

// Simple data class for the preview (so we don't depend on Android Location object)
data class GeoPoint(val lat: Double, val lng: Double)

@Composable
fun RoutePreview(
    pathPoints: List<GeoPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFFFFD740) // Amber/Yellow
) {
    if (pathPoints.size < 2) {
        Box(modifier = modifier.fillMaxWidth().aspectRatio(1.5f), contentAlignment = Alignment.Center) {
            Text("No path data", style = MaterialTheme.typography.labelSmall)
        }
        return
    }

    // 1. Calculate Bounds (Bounding Box)
    val bounds = remember(pathPoints) {
        var minLat = Double.MAX_VALUE
        var maxLat = Double.MIN_VALUE
        var minLng = Double.MAX_VALUE
        var maxLng = Double.MIN_VALUE

        pathPoints.forEach { p ->
            if (p.lat < minLat) minLat = p.lat
            if (p.lat > maxLat) maxLat = p.lat
            if (p.lng < minLng) minLng = p.lng
            if (p.lng > maxLng) maxLng = p.lng
        }
        // Add tiny padding to prevent divide-by-zero on straight lines
        val latRange = (maxLat - minLat).coerceAtLeast(0.0001)
        val lngRange = (maxLng - minLng).coerceAtLeast(0.0001)

        Quadruple(minLat, minLng, latRange, lngRange)
    }

    val (minLat, minLng, latRange, lngRange) = bounds

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.5f) // Rectangular aspect ratio
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(12.dp) // Inner padding so line doesn't hit edge
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 2. Scale Logic: Fit the shape inside the canvas while keeping aspect ratio?
            // For a simple preview, stretching to fill usually looks best on small screens.

            val path = Path().apply {
                pathPoints.forEachIndexed { i, p ->
                    // Normalize (0.0 to 1.0)
                    val normX = ((p.lng - minLng) / lngRange).toFloat()
                    // Flip Y because Latitude goes UP but Canvas Y goes DOWN
                    val normY = 1f - ((p.lat - minLat) / latRange).toFloat()

                    val x = normX * w
                    val y = normY * h

                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
            }

            // 3. Draw the Route
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 4. Draw Start (Green) and End (Red) dots
            val first = pathPoints.first()
            val last = pathPoints.last()

            val startX = ((first.lng - minLng) / lngRange).toFloat() * w
            val startY = (1f - ((first.lat - minLat) / latRange).toFloat()) * h

            val endX = ((last.lng - minLng) / lngRange).toFloat() * w
            val endY = (1f - ((last.lat - minLat) / latRange).toFloat()) * h

            // Start Dot
            drawCircle(Color.Green, radius = 5.dp.toPx(), center = Offset(startX, startY))

            // End Dot
            drawCircle(Color.Red, radius = 5.dp.toPx(), center = Offset(endX, endY))
        }
    }
}

// Helper tuple
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)