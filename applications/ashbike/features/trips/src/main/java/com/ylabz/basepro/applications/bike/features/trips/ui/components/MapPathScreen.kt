package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(240.dp),
    backgroundColor: Color = Color(0xFFA8D5BA),
    pathColor: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Float = 6f,
    inset: Dp = 12.dp,
    markerSize: Dp = 20.dp
) {
    BoxWithConstraints(modifier = modifier) {
        val bc = this
        val density = LocalDensity.current
        val wPx = with(density) {bc.maxWidth .toPx() }
        val hPx = with(density) { bc.maxHeight.toPx() }
        val insetPx = with(density) { inset.toPx() }

        if (locations.size >= 2) {
            // 1) geographic bounds
            val lats = locations.map { it.latitude }
            val lngs = locations.map { it.longitude }
            val minLat = lats.minOrNull()!!
            val maxLat = lats.maxOrNull()!!
            val minLng = lngs.minOrNull()!!
            val maxLng = lngs.maxOrNull()!!
            val latRng = (maxLat - minLat).takeIf { it != 0.0 } ?: 1.0
            val lngRng = (maxLng - minLng).takeIf { it != 0.0 } ?: 1.0

            // 2) projection from geo → pixel Offset
            fun projectPx(lat: Double, lng: Double): Offset {
                val x = insetPx + ((lng - minLng) / lngRng * (wPx - 2*insetPx)).toFloat()
                val y = insetPx + ((maxLat - lat) / latRng * (hPx - 2*insetPx)).toFloat()
                return Offset(x, y)
            }

            // 3) start/end Offsets
            val startOff = projectPx(locations.first().latitude, locations.first().longitude)
            val endOff   = projectPx(locations.last().latitude,  locations.last().longitude)

            // 4) compute a “nice” scale-bar
            val midLat = (minLat + maxLat) / 2.0
            // meters per degree longitude ≈ cos(lat) * 111.32 km
            val mPerDeg = cos(Math.toRadians(midLat)) * 111_320.0
            val totalLngM = lngRng * mPerDeg
            val mPerPx = totalLngM / (wPx - 2*insetPx)
            val targetPx = (wPx - 2*insetPx) * 0.25f               // want ~25% width
            val targetM  = targetPx * mPerPx
            val niceM    = niceDistance(targetM)
            val scalePx  = (niceM / mPerPx).toFloat()
            val scaleDp  = with(density) { scalePx.toDp() }
            val scaleLabel = if (niceM >= 1000)
                "%.1f km".format(niceM/1000.0)
            else
                "${niceM.toInt()} m"

            Box(modifier = Modifier.fillMaxSize()) {
                // —— draw the path on a Canvas ——
                Canvas(modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                ) {
                    val p = Path().apply {
                        val first = projectPx(locations[0].latitude, locations[0].longitude)
                        moveTo(first.x, first.y)
                        locations.drop(1).forEach {
                            val pt = projectPx(it.latitude, it.longitude)
                            lineTo(pt.x, pt.y)
                        }
                    }
                    drawPath(
                        path = p,
                        color = pathColor,
                        style = Stroke(
                            width = strokeWidth,
                            cap   = StrokeCap.Round,
                            join  = StrokeJoin.Round
                        )
                    )
                }

                // —— scale-bar legend ——
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

                // —— start + stop icons ——
                Icon(
                    imageVector   = Icons.Filled.PlayArrow,
                    contentDescription = "Start",
                    tint          = pathColor,
                    modifier      = Modifier
                        .size(markerSize)
                        .offset {
                            IntOffset(
                                (startOff.x - markerSize.toPx()/2).roundToInt(),
                                (startOff.y - markerSize.toPx()/2).roundToInt()
                            )
                        }
                )
                Icon(
                    imageVector   = Icons.Filled.Stop,
                    contentDescription = "End",
                    tint          = pathColor,
                    modifier      = Modifier
                        .size(markerSize)
                        .offset {
                            IntOffset(
                                (endOff.x - markerSize.toPx()/2).roundToInt(),
                                (endOff.y - markerSize.toPx()/2).roundToInt()
                            )
                        }
                )
            }
        } else {
            // just an empty green box if no path
            Box(
                Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )
        }
    }
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
    MapPathScreen(locations = locations)
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
        backgroundColor = Color(0xFFA8D5BA),              // same soft-green
        pathColor       = MaterialTheme.colorScheme.primary,
        strokeWidth     = 6f,
        inset           = 12.dp,
        markerSize      = 20.dp
    )
}