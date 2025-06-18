package com.ylabz.basepro.applications.bike.features.trips.ui.components.maps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.trips.ui.components.LatLngWithElev
import com.ylabz.basepro.applications.bike.features.trips.ui.components.unused.haversineMeters

// reuse your haversineMeters(...) from before

@Composable
fun ElevationProfile(
    points: List<LatLngWithElev>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(100.dp),
    lineColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = Color.White.copy(alpha = 0.3f),
    markerColor: Color = MaterialTheme.colorScheme.secondary,
    topColor: Color = Color(0xFF2196F3),
    bottomColor: Color = Color(0xFFA52A2A)
) {
    if (points.size < 2) return

    // 1) build cumulative (distance, elevation) list
    val distElev = remember(points) {
        val out = mutableListOf<Pair<Float, Float>>()
        var cum = 0f
        out += 0f to points[0].elevation
        for (i in 1 until points.size) {
            val prev = points[i - 1]
            val curr = points[i]
            cum += haversineMeters(
                prev.latLng.latitude, prev.latLng.longitude,
                curr.latLng.latitude, curr.latLng.longitude
            ).toFloat()
            out += cum to curr.elevation
        }
        out
    }

    // 2) compute mins/maxes
    val totalDist = distElev.last().first
    val minElev   = distElev.minOf { it.second }
    val maxElev   = distElev.maxOf { it.second }
    val elevRange = (maxElev - minElev).takeIf { it > 0f } ?: 1f

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // a) sky & earth fill
            drawRect(color = topColor, size = size)
            // build Offset list
            val pts = distElev.map { (dist, elev) ->
                Offset(
                    x = (dist / totalDist) * w,
                    y = ((maxElev - elev) / elevRange) * h
                )
            }
            // path for under-curve
            val under = Path().apply {
                moveTo(pts.first().x, pts.first().y)
                pts.drop(1).forEach { lineTo(it.x, it.y) }
                lineTo(pts.last().x, h)
                lineTo(pts.first().x, h)
                close()
            }
            clipPath(under) {
                drawRect(color = bottomColor, size = size)
            }

            // b) grid lines
            repeat(5) { i ->
                val y = h * i / 4f
                drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
            }
            repeat(5) { i ->
                val x = w * i / 4f
                drawLine(gridColor, Offset(x, h), Offset(x, h - 6.dp.toPx()), strokeWidth = 2f)
            }

            // c) elevation curve
            val curve = Path().apply {
                moveTo(pts.first().x, pts.first().y)
                pts.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(curve, color = lineColor, style = Stroke(width = 3f, cap = StrokeCap.Round))

            // d) max-elevation marker
            val maxIdx = distElev.indexOfFirst { it.second == maxElev }
            val maxOff = pts[maxIdx]
            drawCircle(color = markerColor, radius = 4.dp.toPx(), center = maxOff)
        }

        // e) axis labels
        Text(
            text = "0 m",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
        )
        Text(
            text = "%.1f km".format(totalDist / 1000f),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ElevationProfilePreview() {
    val sample = listOf(
        LatLngWithElev(LatLng(0.0, 0.0), 10f),
        LatLngWithElev(LatLng(0.0, 0.0), 50f),
        LatLngWithElev(LatLng(0.0, 0.0), 30f),
        LatLngWithElev(LatLng(0.0, 0.0), 100f),
        LatLngWithElev(LatLng(0.0, 0.0), 20f),
    )
    ElevationProfile(points = sample, modifier = Modifier.size(320.dp, 100.dp))
}


