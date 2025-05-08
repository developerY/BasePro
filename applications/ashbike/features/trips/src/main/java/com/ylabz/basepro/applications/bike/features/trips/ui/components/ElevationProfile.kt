package com.ylabz.basepro.applications.bike.features.trips.ui.components

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import java.util.UUID
import kotlin.collections.all
import kotlin.math.*

// reuse your haversineMeters(...) from before

@Composable
fun ElevationProfile(
    locations: List<RideLocationEntity>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(100.dp),          // a little taller for labels
    lineColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = Color.White.copy(alpha = 0.3f),
    markerColor: Color = MaterialTheme.colorScheme.secondary,
    topColor: Color = Color(0xFF2196F3),
    bottomColor: Color = Color(0xFFA52A2A)
) {
    if (locations.size < 2 || locations.all { it.elevation == null }) return

    // build (cumDist, elev) pairs
    val points = remember(locations) {
        val list = mutableListOf<Pair<Float, Float>>()
        var cum = 0f
        list += 0f to (locations[0].elevation ?: 0f)
        for (i in 1 until locations.size) {
            val prev = locations[i - 1]
            val curr = locations[i]
            cum += haversineMeters(prev.lat, prev.lng, curr.lat, curr.lng).toFloat()
            list += cum to (curr.elevation ?: 0f)
        }
        list
    }

    val totalDist = points.last().first
    val minElev    = points.minOf { it.second }
    val maxElev    = points.maxOf { it.second }
    val elevRange  = (maxElev - minElev).takeIf { it>0 } ?: 1f

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1) sky & earth
            // draw full blue
            drawRect(color = topColor, size = size)
            // build path for under‐curve
            val pts = points.map { (dist,elev) ->
                val x = (dist/totalDist)*w
                val y = ((maxElev-elev)/elevRange)*h
                Offset(x,y)
            }
            val under = Path().apply {
                moveTo(pts.first().x, pts.first().y)
                pts.drop(1).forEach { lineTo(it.x, it.y) }
                lineTo(pts.last().x, h)
                lineTo(pts.first().x, h)
                close()
            }
            // clip & fill brown
            clipPath(under) {
                drawRect(color = bottomColor, size = size)
            }

            // 2) grid lines
            // horizontal
            repeat(5) { i ->
                val y = h * i/4f
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end   = Offset(w, y),
                    strokeWidth = 1f
                )
            }
            // vertical ticks (not full lines)
            repeat(5) { i ->
                val x = w * i/4f
                drawLine(gridColor, Offset(x, h), Offset(x, h-6.dp.toPx()), strokeWidth = 2f)
            }

            // 3) elevation curve
            val curve = Path().apply {
                moveTo(pts.first().x, pts.first().y)
                pts.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(curve, color = lineColor, style = Stroke(width = 3f, cap = StrokeCap.Round))

            // 4) max‐elev marker
            val maxIdx = points.indexOfFirst { it.second == maxElev }
            val maxOff = pts[maxIdx]
            drawCircle(markerColor, radius = 4.dp.toPx(), center = maxOff)
        }

        /* 5) max elevation label
        Text(
            text = "${maxElev.toInt()} m",
            style = MaterialTheme.typography.bodySmall,
            color = markerColor,
            modifier = Modifier
                .offset {
                    IntOffset(
                        pts[maxIdx].x.roundToInt() + 4.dp.roundToPx(),
                        pts[maxIdx].y.roundToInt() - 20.dp.roundToPx()
                    )
                }
        )*/

        // 6) axis labels
        Text(
            text = "0 m",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
        )
        Text(
            text = "%.1f km".format(totalDist/1000f),
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
fun ElevationProfileEnhancedPreview() {
    val sample = listOf(
        RideLocationEntity(0, "r", 0L, 0.0, 0.0, 10f),
        RideLocationEntity(1, "r", 1L, 0.0, 0.0, 50f),
        RideLocationEntity(2, "r", 2L, 0.0, 0.0, 30f),
        RideLocationEntity(3, "r", 3L, 0.0, 0.0, 100f),
        RideLocationEntity(4, "r", 4L, 0.0, 0.0, 20f)
    )
    ElevationProfile(
        locations = sample,
        modifier = Modifier
            .size(width = 320.dp, height = 100.dp)
    )
}


@Preview
@Composable
fun ElevationProfilePreview() {
 val locations = listOf(
     RideLocationEntity(
         id = 0, rideId = "0", timestamp = 0, lat = 0.0, lng = 0.0,
         elevation = 100f
     ),
     RideLocationEntity(
         id = 1, rideId = "1", timestamp = 1, lat = 1.0, lng = 1.0,
         elevation = 150f
     ),
     RideLocationEntity(
         id = 2, rideId = "2", timestamp = 2, lat = 2.0, lng = 2.0,
         elevation = 120f
     )
 )

 ElevationProfile(locations = locations)
}



@Preview(showBackground = true)
@Composable
fun ElevationProfilePreviewOne() {
    // Sample ride with varying elevations
    val sampleElevations = listOf(
        RideLocationEntity(
            id = 0,
            rideId = UUID.randomUUID().toString(),
            timestamp = 0L,
            lat = 37.78, lng = -122.42,
            elevation = 10f
        ),
        RideLocationEntity(
            id = 1,
            rideId = UUID.randomUUID().toString(),
            timestamp = 1L,
            lat = 37.781, lng = -122.419,
            elevation = 20f
        ),
        RideLocationEntity(
            id = 2,
            rideId = UUID.randomUUID().toString(),
            timestamp = 2L,
            lat = 37.782, lng = -122.418,
            elevation = 15f
        ),
        RideLocationEntity(
            id = 3,
            rideId = UUID.randomUUID().toString(),
            timestamp = 3L,
            lat = 37.783, lng = -122.417,
            elevation = 25f
        ),
        RideLocationEntity(
            id = 4,
            rideId = UUID.randomUUID().toString(),
            timestamp = 4L,
            lat = 37.784, lng = -122.416,
            elevation = 5f
        )
    )

    ElevationProfile(
        locations = sampleElevations,
        modifier = Modifier
            .size(width = 320.dp, height = 80.dp)
    )
}
