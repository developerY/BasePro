package com.ylabz.basepro.applications.bike.features.trips.ui.components.unused

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.trips.ui.components.GpsFix
import com.ylabz.basepro.applications.bike.features.trips.ui.components.haversineMeters
import com.ylabz.basepro.core.model.yelp.BusinessInfo
import com.ylabz.basepro.core.model.yelp.Category
import com.ylabz.basepro.core.model.yelp.Coordinates
import kotlin.math.roundToInt

@Composable
fun MapPathScreen(
    fixes: List<GpsFix>,
    businesses: List<BusinessInfo>,
    placeName: String,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(240.dp),
    backgroundGradient: Brush = Brush.verticalGradient(
        colors = listOf(Color(0xFFC6E2CC), Color(0xFFA8D5BA))
    ),
    gridColor: Color = Color.White,
    slowColor: Color = Color(0xFFFFEB3B),
    fastColor: Color = Color(0xFFBB190C),
    cafeIconColor: Color = Color(0xFFD32F2F), // A reddish color for pins
    strokeWidth: Float = 6f,
    inset: Dp = 12.dp,
    markerSize: Dp = 20.dp,
    pinSize: Dp = 32.dp,
    compassSize: Dp = 24.dp
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        BoxWithConstraints {
            val bc = this
            val wPx = with(LocalDensity.current) { bc.maxWidth.toPx() }
            val hPx = with(LocalDensity.current) { bc.maxHeight.toPx() }
            val insetPx = with(LocalDensity.current) { inset.toPx() }
            val density = LocalDensity.current

            // 1) background + grid
            Canvas(Modifier.fillMaxSize()) {
                drawRect(brush = backgroundGradient)
                val cols = 10; val rows = 10
                val stepX = size.width / cols
                val stepY = size.height / rows
                repeat(cols - 1) { i ->
                    val x = stepX * (i + 1)
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = if ((i + 1) % 5 == 0) 1.5f else 0.8f
                    )
                }
                repeat(rows - 1) { j ->
                    val y = stepY * (j + 1)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = if ((j + 1) % 5 == 0) 1.5f else 0.8f
                    )
                }
            }

            // 2) place label
            Text(
                text = placeName,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // 3) N + compass
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Text("N", style = MaterialTheme.typography.labelSmall.copy(color = Color.Black.copy(alpha = 0.7f)))
                Icon(
                    imageVector = Icons.Default.North,
                    contentDescription = "North",
                    tint = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.size(compassSize)
                )
            }

            if (fixes.size >= 2) {
                // project lat/lng → screen
                val lats = fixes.map { it.lat }
                val lngs = fixes.map { it.lng }
                val minLat = lats.minOrNull()!!;
                val maxLat = lats.maxOrNull()!!
                val minLng = lngs.minOrNull()!!;
                val maxLng = lngs.maxOrNull()!!
                val latRange = (maxLat - minLat).takeIf { it > 0 } ?: 1.0
                val lngRange = (maxLng - minLng).takeIf { it > 0 } ?: 1.0
                fun project(lat: Double, lng: Double) = Offset(
                    x = insetPx + ((lng - minLng) / lngRange * (wPx - 2 * insetPx)).toFloat(),
                    y = insetPx + ((maxLat - lat) / latRange * (hPx - 2 * insetPx)).toFloat()
                )

                data class Seg(val a: Offset, val b: Offset, val speed: Double)
                val segments = fixes.zipWithNext().map { (p0, p1) ->
                    val a = project(p0.lat, p0.lng)
                    val b = project(p1.lat, p1.lng)
                    val dM = haversineMeters(p0.lat, p0.lng, p1.lat, p1.lng)
                    val dt = ((p1.timeMs - p0.timeMs) / 1000.0).coerceAtLeast(0.1)
                    val kmh = dM / 1000.0 / (dt / 3600.0)
                    Seg(a, b, kmh)
                }
                val minSpeed = segments.minOf { it.speed }
                val maxSpeed = segments.maxOf { it.speed }.coerceAtLeast(minSpeed + 0.1)

                // 4) draw path
                Canvas(Modifier.fillMaxSize()) {
                    segments.forEach { seg ->
                        val t = ((seg.speed - minSpeed) / (maxSpeed - minSpeed)).toFloat()
                        val col = lerp(slowColor, fastColor, t)
                        drawLine(col, seg.a, seg.b, strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    }
                }

                // 5) start/end icons
                val start = segments.first().a
                val end = segments.last().b
                Icon(
                    Icons.Filled.PlayArrow, "Start",
                    tint = slowColor,
                    modifier = Modifier
                        .size(markerSize)
                        .offset {
                            IntOffset(
                                (start.x - markerSize.toPx() / 2).roundToInt(),
                                (start.y - markerSize.toPx() / 2).roundToInt()
                            )
                        }
                )
                Icon(
                    Icons.Filled.Stop, "End",
                    tint = fastColor,
                    modifier = Modifier
                        .size(markerSize)
                        .offset {
                            IntOffset(
                                (end.x - markerSize.toPx() / 2).roundToInt(),
                                (end.y - markerSize.toPx() / 2).roundToInt()
                            )
                        }
                )

                // 6) Draw business pins and labels
                businesses.forEach { business ->
                    business.coordinates?.let { coords ->
                        val position = project(coords?.latitude?.toDouble() ?: 0.0, coords?.longitude?.toDouble() ?: 0.0)
                        val pinOffset = with(LocalDensity.current) {
                            IntOffset(
                                x = (position.x - pinSize.toPx() / 2).roundToInt(),
                                y = (position.y - pinSize.toPx()).roundToInt()
                            )
                        }
                        Column(
                            modifier = Modifier.offset { pinOffset },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = business.name,
                                tint = cafeIconColor,
                                modifier = Modifier.size(pinSize)
                            )
                            business.name?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Black,
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun MapPathScreenPreview() {
    val fixes = listOf(
        GpsFix(lat = 34.0522, lng = -118.2437, elevation = 0f, timeMs = 0, speedMps = 0f),
        GpsFix(lat = 34.0530, lng = -118.2440, elevation = 0f, timeMs = 1000, speedMps = 5f),
        GpsFix(lat = 34.0540, lng = -118.2450, elevation = 0f, timeMs = 2000, speedMps = 10f),
    )
    val businesses = listOf(
        BusinessInfo(
            id = "1",
            name = "The Friendly Bean",
            url = "http://www.friendlybean.com",
            rating = 4.5,
            photos = emptyList(),
            price = "$$",
            coordinates = Coordinates(latitude = 34.0535, longitude = -118.2445),
            categories = listOf(Category(title = "Coffee & Tea"))
        )
    )
    MapPathScreen(
        fixes = fixes,
        businesses = businesses,
        placeName = "Sample Ride"
    )
}

// Dummy data for preview
data class GpsFix(
    val lat: Double,
    val lng: Double,
    val elevation: Float,
    val timeMs: Long,
    val speedMps: Float
)

fun haversineMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val R = 6371e3
    val phi1 = Math.toRadians(lat1)
    val phi2 = Math.toRadians(lat2)
    val deltaPhi = Math.toRadians(lat2 - lat1)
    val deltaLambda = Math.toRadians(lng2 - lng1)
    val a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
            Math.cos(phi1) * Math.cos(phi2) *
            Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return R * c
}

 */