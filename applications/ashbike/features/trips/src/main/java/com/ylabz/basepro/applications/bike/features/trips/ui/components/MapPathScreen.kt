package com.ylabz.basepro.applications.bike.features.trips.ui.components

import android.R.attr.end
import android.R.attr.inset
import android.R.attr.strokeWidth
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.trips.ui.components.unused.haversineMeters
import com.ylabz.basepro.core.model.yelp.BusinessInfo
import kotlin.math.*

@Composable
fun MapPathScreen(
    fixes: List<GpsFix>,
    coffeeShops: List<BusinessInfo> = emptyList(),
    placeName: String,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(240.dp),
    backgroundGradient: Brush = Brush.verticalGradient(
        colors = listOf(Color(0xFFC6E2CC), Color(0xFFA8D5BA))
    ),
    gridColor: Color = Color.White,//.copy(alpha = 0.15f),
    slowColor: Color = Color(0xFFFFEB3B),//Color.Gray,                  // your slow‐speed color
    fastColor: Color = Color(0xFFBB190C),//MaterialTheme.colorScheme.primary, // your fast‐speed color
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
            val wPx     = with(LocalDensity.current) { bc.maxWidth.toPx() }
            val hPx     = with(LocalDensity.current) { bc.maxHeight.toPx() }
            val insetPx = with(LocalDensity.current) { inset.toPx() }
            val density = LocalDensity.current

            // 1) background + grid
            Canvas(Modifier.fillMaxSize()) {
                drawRect(brush = backgroundGradient)
                val cols = 10; val rows = 10
                val stepX = size.width  / cols
                val stepY = size.height / rows
                repeat(cols - 1) { i ->
                    val x = stepX * (i + 1)
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end   = Offset(x, size.height),
                        strokeWidth = if ((i+1)%5==0) 1.5f else 0.8f
                    )
                }
                repeat(rows - 1) { j ->
                    val y = stepY * (j + 1)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end   = Offset(size.width, y),
                        strokeWidth = if ((j+1)%5==0) 1.5f else 0.8f
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

                val rideLats = fixes.map { it.lat }
                val rideLngs = fixes.map { it.lng }
                val cafeLats = coffeeShops.map { it.coordinates?.latitude ?: 0.0 }
                val cafeLngs = coffeeShops.map { it.coordinates?.longitude ?: 0.0 }

                val allLats = rideLats + cafeLats
                val allLngs = rideLngs + cafeLngs

// Safety check for empty lists

                if (allLats.isEmpty() || allLngs.isEmpty()) return@BoxWithConstraints

                val minLat = allLats.minOrNull()!!; val maxLat = allLats.maxOrNull()!!
                val minLng = allLngs.minOrNull()!!; val maxLng = allLngs.maxOrNull()!!
                // --- END FIX ---

                val latRange = (maxLat - minLat).takeIf { it > 0 } ?: 1.0
                val lngRange = (maxLng - minLng).takeIf { it > 0 } ?: 1.0

                fun project(lat: Double, lng: Double): Offset {
                    val x = insetPx + ((lng - minLng) / lngRange * (wPx - 2 * insetPx)).toFloat()
                    val y = insetPx + ((maxLat - lat) / latRange * (hPx - 2 * insetPx)).toFloat()
                    return Offset(x, y)
                }


                coffeeShops.forEach { business ->
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
                                tint = Color.Black.copy(alpha = 0.3f),
                                //tint = cafeIconColor,
                                modifier = Modifier.size(pinSize)
                            )
                            business.name?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Black.copy(alpha = 0.3f),
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }


                // compute real‐world distance scale (meters → px)
                val midLat   = (minLat + maxLat)/2
                val boxWidthM= haversineMeters(midLat, minLng, midLat, maxLng)
                val mPerPx   = boxWidthM / (wPx - 2*insetPx)
                val targetPx = (wPx - 2*insetPx)*0.25f   // want ~25% width
                val targetM  = targetPx * mPerPx
                val niceM    = niceDistance(targetM)
                val scalePx  = (niceM / mPerPx).toFloat()
                val scaleDp  = with(density) { scalePx.toDp() }
                val scaleLabel = if (niceM >= 1000) "%.1f km".format(niceM/1000) else "${niceM.toInt()} m"

                // build speed‐colored segments
                data class Seg(val a: Offset, val b: Offset, val speed: Double)
                val segments = fixes.zipWithNext().map { (p0,p1) ->
                    val a    = project(p0.lat, p0.lng)
                    val b    = project(p1.lat, p1.lng)
                    val dM   = haversineMeters(p0.lat,p0.lng,p1.lat,p1.lng)
                    val dt   = ((p1.timeMs - p0.timeMs)/1000.0).coerceAtLeast(0.1)
                    val kmh  = dM/1000.0 / (dt/3600.0)
                    Seg(a,b,kmh)
                }
                val minSpeed = segments.minOf { it.speed }
                val maxSpeed = segments.maxOf { it.speed }.coerceAtLeast(minSpeed+0.1)

                // 4) draw path
                Canvas(Modifier.fillMaxSize()) {
                    segments.forEach { seg ->
                        val t = ((seg.speed - minSpeed)/(maxSpeed - minSpeed)).toFloat()
                        val col = lerp(slowColor, fastColor, t)
                        drawLine(col, seg.a, seg.b, strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    }
                }

                // 5) start/end icons
                val start = segments.first().a
                val end   = segments.last().b
                Icon(Icons.Filled.PlayArrow, "Start",
                    tint = slowColor,
                    modifier = Modifier
                        .size(markerSize)
                        .offset {
                            IntOffset(
                                (start.x - markerSize.toPx()/2).roundToInt(),
                                (start.y - markerSize.toPx()/2).roundToInt()
                            )
                        }
                )
                Icon(Icons.Filled.Stop, "End",
                    tint = fastColor,
                    modifier = Modifier
                        .size(markerSize)
                        .offset {
                            IntOffset(
                                (end.x   - markerSize.toPx()/2).roundToInt(),
                                (end.y   - markerSize.toPx()/2).roundToInt()
                            )
                        }
                )



                // G) **distance scale** at bottom‐start
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .height(2.dp)
                            .width(scaleDp)
                            .background(Color.Black)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = scaleLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }



                // 7) **speed legend** at bottom‐end
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(width = 60.dp, height = 4.dp)
                            .background(
                                brush = Brush.horizontalGradient(listOf(slowColor, fastColor)),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("${minSpeed.roundToInt()} km/h", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.width(8.dp))
                    Text("${maxSpeed.roundToInt()} km/h", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// -- helper functions below (same as before) --

fun haversineMeters(
    lat1: Double, lng1: Double,
    lat2: Double, lng2: Double
): Double {
    val R = 6_371_000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat/2).pow(2) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLng/2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}

private fun niceDistance(m: Double): Double {
    val exp  = floor(log10(m.coerceAtLeast(1.0)))
    val base = 10.0.pow(exp)
    val d    = m / base
    val nice = when {
        d < 1.5 -> 1.0
        d < 3.0 -> 2.0
        d < 7.0 -> 5.0
        else    -> 10.0
    }
    return nice * base
}

@Preview(showBackground = true)
@Composable
fun MapPathScreenPreview() {
    val fixes = listOf(
        GpsFix(lat = 34.0522, lng = -118.2437, elevation = 0f, timeMs = 0, speedMps = 0f),
        GpsFix(lat = 34.0530, lng = -118.2440, elevation = 0f, timeMs = 1000, speedMps = 5f),
        GpsFix(lat = 34.0540, lng = -118.2450, elevation = 0f, timeMs = 2000, speedMps = 10f),
        GpsFix(lat = 34.0550, lng = -118.2460, elevation = 0f, timeMs = 3000, speedMps = 15f),
        GpsFix(lat = 34.0560, lng = -118.2470, elevation = 0f, timeMs = 4000, speedMps = 20f)
    )
    MapPathScreen(
        fixes = fixes,
        placeName = "Sample Place"
    )
}
