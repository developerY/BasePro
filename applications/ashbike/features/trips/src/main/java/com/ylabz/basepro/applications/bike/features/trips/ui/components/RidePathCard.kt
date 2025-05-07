package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng

@Composable
fun RidePathCard(
    path: List<LatLng>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(240.dp),
    backgroundColor: Color = Color(0xFFA8D5BA),
    lineColor: Color = MaterialTheme.colorScheme.primary,
    lineWidth: Float = 6f,
    startMarkerColor: Color = MaterialTheme.colorScheme.primary,
    endMarkerColor: Color = MaterialTheme.colorScheme.error,
    markerRadius: Dp = 6.dp
) {
    Card(
        modifier = modifier,
        shape = CardDefaults.shape, // keep your RoundedCornerShape(12.dp) if you like
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
            ) {
                if (path.size < 2) return@Canvas

                // 1) compute geographic bounds
                val lats = path.map { it.latitude }
                val lngs = path.map { it.longitude }
                val minLat = lats.minOrNull()!!
                val maxLat = lats.maxOrNull()!!
                val minLng = lngs.minOrNull()!!
                val maxLng = lngs.maxOrNull()!!

                val latRange = (maxLat - minLat).takeIf { it != 0.0 } ?: 1.0
                val lngRange = (maxLng - minLng).takeIf { it != 0.0 } ?: 1.0

                // 2) project each LatLng → canvas Offset
                fun project(lat: Double, lng: Double): Offset {
                    val x = ((lng - minLng) / lngRange * size.width).toFloat()
                    // invert Y so north is up
                    val y = ((maxLat - lat) / latRange * size.height).toFloat()
                    return Offset(x, y)
                }

                // 3) build a Path
                val drawPath = Path().apply {
                    val first = project(path[0].latitude, path[0].longitude)
                    moveTo(first.x, first.y)
                    path.drop(1).forEach {
                        val pt = project(it.latitude, it.longitude)
                        lineTo(pt.x, pt.y)
                    }
                }

                // 4) stroke it with rounded caps/joins
                drawPath(
                    path = drawPath,
                    color = lineColor,
                    style = Stroke(
                        width = lineWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 5) draw start/end circles
                val markerPx = markerRadius.toPx()
                val start = project(path.first().latitude, path.first().longitude)
                val end   = project(path.last().latitude,  path.last().longitude)

                drawCircle(
                    color = startMarkerColor,
                    radius = markerPx,
                    center = start
                )
                drawCircle(
                    color = endMarkerColor,
                    radius = markerPx,
                    center = end
                )
            }
        }
    }
}
