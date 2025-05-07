package com.ylabz.basepro.applications.bike.features.trips.ui.components

import android.R.attr.end
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

@Composable
fun MapPathScreen(
    locations: List<LatLng>,
    placeName: String,                        // new!
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(240.dp),
    backgroundGradient: Brush = Brush.verticalGradient(
        colors = listOf(Color(0xFFC6E2CC), Color(0xFFA8D5BA))
    ),
    gridColor: Color = Color.White.copy(alpha = 0.15f),
    pathColor: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Float = 6f,
    inset: Dp = 12.dp,
    markerSize: Dp = 20.dp,
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
            Canvas(modifier = Modifier.fillMaxSize()) {
                // gradient fill
                drawRect(brush = backgroundGradient)

                // subtle grid
                // replace your “subtle grid” section with this:

                // subtle grid with minor/major lines
                val cols = 10
                val rows = 10
                val stepX = size.width  / cols
                val stepY = size.height / rows

                for (i in 1 until cols) {
                    val x = stepX * i
                    // every 5th line is “major”
                    val alpha = if (i % 5 == 0) 0.25f else 0.15f
                    drawLine(
                        color = Color.White,//.copy(alpha = alpha),
                        start = Offset(x, 0f),
                        end   = Offset(x, size.height),
                        strokeWidth = if (i % 5 == 0) 1.5f else 0.8f
                    )
                }
                for (j in 1 until rows) {
                    val y = stepY * j
                    val alpha = if (j % 5 == 0) 0.25f else 0.15f
                    drawLine(
                        color = Color.White,//.copy(alpha = alpha),
                        start = Offset(0f, y),
                        end   = Offset(size.width, y),
                        strokeWidth = if (j % 5 == 0) 1.5f else 0.8f
                    )
                }

            }

            // 2) city/place label
            Text(
                text = placeName,
                style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // 3) compass rose
            Icon(
                imageVector = Icons.Default.North, // or pick any compass icon
                contentDescription = "North",
                tint = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(compassSize)
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )

            if (locations.size >= 2) {
                // reuse your projection + path logic here
                val lats = locations.map { it.latitude }
                val lngs = locations.map { it.longitude }
                val minLat = lats.minOrNull()!!;
                val maxLat = lats.maxOrNull()!!
                val minLng = lngs.minOrNull()!!;
                val maxLng = lngs.maxOrNull()!!
                val latR = (maxLat - minLat).takeIf { it != 0.0 } ?: 1.0
                val lngR = (maxLng - minLng).takeIf { it != 0.0 } ?: 1.0
                val latRng = (maxLat - minLat).takeIf { it != 0.0 } ?: 1.0
                val lngRng = (maxLng - minLng).takeIf { it != 0.0 } ?: 1.0


                fun project(lat: Double, lng: Double) = Offset(
                    x = insetPx + ((lng - minLng) / lngR * (wPx - 2 * insetPx)).toFloat(),
                    y = insetPx + ((maxLat - lat) / latR * (hPx - 2 * insetPx)).toFloat()
                )

                val startOff = project(locations.first().latitude, locations.first().longitude)
                val endOff = project(locations.last().latitude, locations.last().longitude)

                // 4) compute a “nice” scale-bar
                // … then in your Composable:
                val midLat = (minLat + maxLat) / 2
                // real-world width of the box in metres:
                val boxWidthM = haversineMeters(midLat, minLng, midLat, maxLng)
                val mPerPx   = boxWidthM / (wPx - 2 * insetPx)
                // meters per degree longitude ≈ cos(lat) * 111.32 km
                val mPerDeg = cos(Math.toRadians(midLat)) * 111_320.0
                val totalLngM = lngRng * mPerDeg
                val targetPx = (wPx - 2 * insetPx) * 0.25f               // want ~25% width
                val targetM = targetPx * mPerPx
                val niceM = niceDistance(targetM)
                val scalePx = (niceM / mPerPx).toFloat()
                val scaleDp = with(density) { scalePx.toDp() }
                val scaleLabel = if (niceM >= 1000)
                    "%.1f km".format(niceM / 1000.0)
                else
                    "${niceM.toInt()} m"

                // 4) draw the path
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        moveTo(startOff.x, startOff.y)
                        locations.drop(1).forEach {
                            val pt = project(it.latitude, it.longitude)
                            lineTo(pt.x, pt.y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = pathColor,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }

                // 5) start & end icons
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Start",
                    tint = pathColor,
                    modifier = Modifier
                        .size(markerSize)
                        .offset {
                            IntOffset(
                                (startOff.x - markerSize.toPx() / 2).roundToInt(),
                                (startOff.y - markerSize.toPx() / 2).roundToInt()
                            )
                        }
                )
                Icon(
                    imageVector = Icons.Filled.Stop,
                    contentDescription = "End",
                    tint = pathColor,
                    modifier = Modifier
                        .size(markerSize)
                        .offset {
                            IntOffset(
                                (endOff.x - markerSize.toPx() / 2).roundToInt(),
                                (endOff.y - markerSize.toPx() / 2).roundToInt()
                            )
                        }
                )


                // 3) **Scale-bar legend**
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .height(2.dp)
                            .width(scaleDp)      // computed earlier
                            .background(Color.Black)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = scaleLabel,     // e.g. “20 m” or “0.5 km”
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }
            }


        }
    }
}

private fun haversineMeters(
    lat1: Double, lng1: Double,
    lat2: Double, lng2: Double
): Double {
    val R = 6_371_000.0 // Earth radius in metres
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat/2).pow(2) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLng/2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}


/** round to 1,2,5 × 10ᵉⁿⁿ */
private fun niceDistance(m: Double): Double {
    val exp   = floor(log10(m.coerceAtLeast(1.0)))
    val base  = 10.0.pow(exp)
    val d     = m / base
    val nice  = when {
        d < 1.5 -> 1.0
        d < 3.0 -> 2.0
        d < 7.0 -> 5.0
        else    -> 10.0
    }
    return nice * base
}

@Preview
@Composable
fun MapPathScreenPreview() {
    val locations = listOf(
        LatLng(37.7749, -122.4194),
        LatLng(34.0522, -118.2437),
        LatLng(40.7128, -74.0060)
    )
    MapPathScreen(locations = locations, placeName = "San Francisco")
}

@Preview(showBackground = true)
@Composable
fun MapPathScreenPreviewSetup() {
    // A little “L-shaped” sample ride
    val samplePath = listOf(
        LatLng(37.78,  -122.42),
        LatLng(37.790, -122.415),
        LatLng(37.785, -122.410),
        LatLng(37.788, -122.405),
        LatLng(37.789, -122.403)
    )

    MapPathScreen(
        locations       = samplePath,
        modifier        = Modifier
            .size(width = 320.dp, height = 240.dp)
            .aspectRatio(4f / 3f),
        //backgroundColor = Color(0xFFA8D5BA),              // same soft-green
        pathColor       = MaterialTheme.colorScheme.primary,
        strokeWidth     = 6f,
        inset           = 12.dp,
        markerSize      = 20.dp,
        placeName       = "San Francisco"
    )
}