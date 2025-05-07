package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import kotlin.collections.drop
import kotlin.collections.map

@Composable
fun MapPathScreen(
    locations: List<LatLng>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFA8D5BA),      // soft map-like green
    pathColor: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Float = 6f
) {
    Box(
        modifier = modifier
            .background(backgroundColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (locations.size < 2) return@Canvas

            // compute bounding box
            val lats = locations.map { it.latitude }
            val lngs = locations.map { it.longitude }
            val minLat = lats.minOrNull()!!
            val maxLat = lats.maxOrNull()!!
            val minLng = lngs.minOrNull()!!
            val maxLng = lngs.maxOrNull()!!

            // avoid zero-division
            val latRange = (maxLat - minLat).takeIf { it != 0.0 } ?: 1.0
            val lngRange = (maxLng - minLng).takeIf { it != 0.0 } ?: 1.0

            // helper to project geo → canvas
            fun project(lat: Double, lng: Double): Offset {
                val x = ((lng - minLng) / lngRange * size.width).toFloat()
                // invert Y so north is "up"
                val y = ((maxLat - lat) / latRange * size.height).toFloat()
                return Offset(x, y)
            }

            // build path
            val path = Path().apply {
                moveTo(
                    project(locations[0].latitude, locations[0].longitude).x,

                    project(locations[0].latitude, locations[0].longitude).y
                )
                locations.drop(1).forEach { loc ->
                    val pt = project(loc.latitude, loc.longitude)
                    lineTo(pt.x, pt.y)
                }
            }

            // draw it
            drawPath(
                path     = path,
                color    = pathColor,
                style    = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PathMapPreview() {
    // sample “ride” around a little square
    val sample = listOf(
        LatLng(37.78, -122.42),
        LatLng(37.79, -122.415),
        LatLng(37.785, -122.41),
        LatLng(37.788, -122.405),
        LatLng(37.789, -122.403)
    )
    MapPathScreen(
        locations = sample,
        modifier = Modifier
            .size(width = 320.dp, height = 240.dp)
            .aspectRatio(4f / 3f)
    )
}
