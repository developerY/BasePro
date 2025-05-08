package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import java.util.UUID
import kotlin.collections.all
import kotlin.math.*

@Composable
fun ElevationProfile(
    locations: List<RideLocationEntity>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(80.dp),
    lineColor: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Float = 2f,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    topColor: Color = Color(0xFF2196F3),    // blue
    bottomColor: Color = Color(0xFFA52A2A)  // brown
) {
    if (locations.size < 2 || locations.all { it.elevation == null }) return

    // Build (distance, elevation) pairs…
    val points = remember(locations) {
        val list = mutableListOf<Pair<Float, Float>>()
        var cumDist = 0f
        list += Pair(0f, locations[0].elevation ?: 0f)
        for (i in 1 until locations.size) {
            val prev = locations[i - 1]
            val curr = locations[i]
            val segment = haversineMeters(
                prev.lat, prev.lng,
                curr.lat, curr.lng
            ).toFloat()
            cumDist += segment
            list += Pair(cumDist, curr.elevation ?: 0f)
        }
        list
    }

    val totalDist = points.last().first
    val minElev   = points.minOf { it.second }
    val maxElev   = points.maxOf { it.second }
    val elevRange = (maxElev - minElev).takeIf { it > 0f } ?: 1f

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1) Compute your elevation‐vs‐distance Offsets:
            val pts: List<Offset> = points.map { (dist, elev) ->
                val x = (dist / totalDist) * w
                val y = ((maxElev - elev) / elevRange) * h
                Offset(x, y)
            }

            // 2) Draw the blue “sky” everywhere:
            drawRect(color = Color(0xFF2196F3), size = size)

            // 3) Build a Path that traces your line, then closes down to the bottom:
            val underPath = Path().apply {
                // start at first point
                moveTo(pts.first().x, pts.first().y)
                // trace the curve
                pts.drop(1).forEach { lineTo(it.x, it.y) }
                // straight line down to bottom‐right
                lineTo(pts.last().x, h)
                // along bottom to bottom‐left
                lineTo(pts.first().x, h)
                close()
            }

            // 4) Clip to that “under” region, and fill it with brown:
            clipPath(underPath) {
                drawRect(color = Color(0xFFA52A2A), size = size)
            }

            // 5) Finally draw your elevation line on top:
            val linePath = Path().apply {
                moveTo(pts.first().x, pts.first().y)
                pts.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(
                path = linePath,
                color = Color.Black,//MaterialTheme.colorScheme.onSurface,
                style = Stroke(width = 2f)
            )
        }


        // 3) max elevation label
        Text(
            text = "${maxElev.toInt()} m",
            style = MaterialTheme.typography.bodySmall,
            color = labelColor,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = 4.dp)
        )
    }
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
