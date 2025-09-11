package com.ylabz.basepro.applications.bike.features.trips.ui.components

////import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ylabz.basepro.applications.bike.features.trips.R
import com.ylabz.basepro.core.model.location.GpsFix
import com.ylabz.basepro.core.model.yelp.BusinessInfo
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random


// Assuming GpsFix is defined in your project.
// data class GpsFix(val lat: Double, val lng: Double, val timeMs: Long, ...)

/**
 * A custom-drawn map component to visualize a GPS path.
 * It does not use any external map libraries.
 *
 * @param fixes The list of GPS points for the ride path. The map will scale to fit this path.
 * @param coffeeShops A list of nearby coffee shops to display as markers.
 * @param onFindCafes A callback invoked when the user requests to find cafes.
 * @param placeName A descriptive name for the location, displayed at the top.
 */
@Composable
fun MapPathScreen(
    fixes: List<GpsFix>,
    coffeeShops: List<BusinessInfo>,
    onFindCafes: () -> Unit,
    placeName: String,
    modifier: Modifier = Modifier,
    backgroundGradient: Brush = Brush.verticalGradient(
        colors = listOf(Color(0xFFC6E2CC), Color(0xFFA8D5BA))
    ),
    gridColor: Color = Color.White.copy(alpha = 0.5f),
    slowColor: Color = Color(0xFFFFEB3B),
    fastColor: Color = Color(0xFFBB190C),
    pathStrokeWidth: Dp = 3.dp,
    inset: Dp = 25.dp, //,16.dp,
    markerSize: Dp = 20.dp,
    pinSize: Dp = 18.dp, //32.dp,
    compassSize: Dp = 24.dp
) {
    var cafesVisible by rememberSaveable { mutableStateOf(false) }
    var cafesFetched by rememberSaveable { mutableStateOf(false) }
    val textMeasurer = rememberTextMeasurer()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            this
            val wPx = constraints.maxWidth.toFloat()
            val hPx = constraints.maxHeight.toFloat()
            val insetPx = with(LocalDensity.current) { inset.toPx() }

            // 1. --- Calculate Bounds based on the ride path ONLY ---
            if (fixes.isNotEmpty()) {
                val minLat = fixes.minOf { it.lat }
                val maxLat = fixes.maxOf { it.lat }
                val minLng = fixes.minOf { it.lng }
                val maxLng = fixes.maxOf { it.lng }

                val latRange = (maxLat - minLat).takeIf { it > 0 } ?: 0.001
                val lngRange = (maxLng - minLng).takeIf { it > 0 } ?: 0.001

                // Function to project geographical coordinates to screen coordinates.
                val project: (Double, Double) -> Offset = { lat, lng ->
                    val x = insetPx + ((lng - minLng) / lngRange * (wPx - 2 * insetPx)).toFloat()
                    val y = insetPx + ((maxLat - lat) / latRange * (hPx - 2 * insetPx)).toFloat()
                    Offset(x, y)
                }

                val pathSegments = remember(fixes, project) {
                    if (fixes.size < 2) emptyList() else {
                        fixes.zipWithNext().map { (p0, p1) ->
                            createPathSegment(p0, p1, project)
                        }
                    }
                }
                val minSpeed = pathSegments.minOfOrNull { it.speedKmh } ?: 0.0
                val maxSpeed =
                    pathSegments.maxOfOrNull { it.speedKmh }?.coerceAtLeast(minSpeed + 0.1) ?: 1.0

                // 2. --- Draw Canvas Content ---
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(brush = backgroundGradient)
                    drawGrid(gridColor, 10, 10)

                    if (cafesVisible) {
                        drawCafeMarkers(coffeeShops, project, pinSize, textMeasurer)
                    }

                    if (pathSegments.isNotEmpty()) {
                        drawRidePath(
                            pathSegments,
                            slowColor,
                            fastColor,
                            pathStrokeWidth.toPx(),
                            minSpeed,
                            maxSpeed
                        )
                    }
                }

                // 3. --- Overlay UI Elements ---
                PlaceNameLabel(placeName, Modifier.align(Alignment.TopCenter))
                Compass(compassSize, Modifier.align(Alignment.TopEnd))

                if (pathSegments.isNotEmpty()) {
                    val start = pathSegments.first().startOffset
                    val end = pathSegments.last().endOffset
                    MapMarker(
                        icon = Icons.Filled.PlayArrow,
                        position = start,
                        size = markerSize,
                        color = slowColor
                    )
                    MapMarker(
                        icon = Icons.Filled.Stop,
                        position = end,
                        size = markerSize,
                        color = fastColor
                    )
                }

                DistanceScale(
                    minLat,
                    minLng,
                    maxLat,
                    maxLng,
                    wPx,
                    insetPx,
                    Modifier.align(Alignment.BottomStart)
                )
                SpeedLegend(
                    minSpeed,
                    maxSpeed,
                    slowColor,
                    fastColor,
                    Modifier.align(Alignment.BottomEnd)
                )

            } else {
                // Fallback view if there's no data
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(brush = backgroundGradient)
                    drawGrid(gridColor, 10, 10)
                }
                Text(
                    stringResource(R.string.feature_trips_map_no_ride_data),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Clickable coffee icon
            FindCafesButton(
                cafesVisible = cafesVisible,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                cafesVisible = !cafesVisible
                if (cafesVisible && !cafesFetched) {
                    onFindCafes()
                    cafesFetched = true
                }
            }
        }
    }
}


// --- Helper Composables & Drawing Functions ---

@Composable
private fun PlaceNameLabel(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Black.copy(alpha = 0.8f),
        modifier = modifier
            .padding(top = 8.dp)
            .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun Compass(size: Dp, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            stringResource(R.string.feature_trips_map_compass_n),
            style = MaterialTheme.typography.labelSmall.copy(color = Color.Black.copy(alpha = 0.7f))
        )
        Icon(
            imageVector = Icons.Default.North,
            contentDescription = stringResource(R.string.feature_trips_map_compass_content_desc_north),
            tint = Color.Black.copy(alpha = 0.7f),
            modifier = Modifier.size(size)
        )
    }
}

@Composable
private fun FindCafesButton(
    cafesVisible: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Coffee,
            contentDescription = stringResource(R.string.feature_trips_map_cafes_button_content_desc),
            tint = if (cafesVisible) MaterialTheme.colorScheme.primary else Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

private data class PathSegment(
    val startOffset: Offset,
    val endOffset: Offset,
    val speedKmh: Double
)

private fun createPathSegment(
    p0: GpsFix,
    p1: GpsFix,
    project: (Double, Double) -> Offset
): PathSegment {
    val distanceMeters = haversineMeters(p0.lat, p0.lng, p1.lat, p1.lng)
    val timeSeconds = (p1.timeMs - p0.timeMs) / 1000.0

    // FIX: Use the more reliable speed from the GpsFix data directly.
    // We'll use the speed from the second point in the pair.
    // val speedMps = p1.speedMps.toDouble()
    val speedMps = if (timeSeconds > 0.1) distanceMeters / timeSeconds else 0.0 // does not work

    return PathSegment(
        startOffset = project(p0.lat, p0.lng),
        endOffset = project(p1.lat, p1.lng),
        speedKmh = speedMps * 3.6
    )
}

private fun DrawScope.drawGrid(color: Color, rows: Int, cols: Int) {
    val stepX = size.width / cols
    val stepY = size.height / rows
    repeat(cols - 1) { i ->
        val x = stepX * (i + 1)
        drawLine(
            color,
            Offset(x, 0f),
            Offset(x, size.height),
            strokeWidth = if ((i + 1) % 5 == 0) 1.5f else 0.8f
        )
    }
    repeat(rows - 1) { j ->
        val y = stepY * (j + 1)
        drawLine(
            color,
            Offset(0f, y),
            Offset(size.width, y),
            strokeWidth = if ((j + 1) % 5 == 0) 1.5f else 0.8f
        )
    }
}


/**
 * Generates a random pastel color for Jetpack Compose.
 */
fun generateRandomPastelColor(): Color {
    // Generate random R, G, and B components in the upper range (128-255)
    // to ensure the color is light and pastel-like.
    val red = Random.nextInt(128) + 128
    val green = Random.nextInt(128) + 128
    val blue = Random.nextInt(128) + 128
    return Color(red, green, blue)
}

private fun DrawScope.drawCafeMarkers(
    cafes: List<BusinessInfo>,
    project: (Double, Double) -> Offset,
    pinSize: Dp,
    textMeasurer: TextMeasurer
) {
    val pinSizePx = pinSize.toPx()
    cafes.forEach { business ->
        val lat = business.coordinates?.latitude ?: return@forEach
        val lng = business.coordinates?.longitude ?: return@forEach
        val position = project(lat, lng)


        // Draw the main marker body with the new random color
        drawCircle(
            color = generateRandomPastelColor(),
            radius = pinSizePx / 2.5f,
            center = position
        )

        // Draw a subtle white border around the circle for definition
        drawCircle(
            style = Stroke(width = pinSizePx * 0.1f),
            color = Color.White.copy(alpha = 0.8f),
            radius = pinSizePx / 2.5f,
            center = position
        )

        // Draw the text label for the cafe below the marker
        val textLayoutResult = textMeasurer.measure(
            text = business.name ?: "",
            style = TextStyle(fontSize = 10.sp, color = Color.Black.copy(alpha = 0.9f))
        )
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = position + Offset(-textLayoutResult.size.width / 2f, pinSizePx / 2f)
        )
    }
}

private fun DrawScope.drawRidePath(
    segments: List<PathSegment>,
    slowColor: Color,
    fastColor: Color,
    strokeWidth: Float,
    minSpeed: Double,
    maxSpeed: Double,
) {
    segments.forEach { seg ->
        val speedFraction =
            ((seg.speedKmh - minSpeed) / (maxSpeed - minSpeed)).toFloat().coerceIn(0f, 1f)
        val color = lerp(slowColor, fastColor, speedFraction)
        drawLine(
            color,
            seg.startOffset,
            seg.endOffset,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun MapMarker(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    position: Offset,
    size: Dp,
    color: Color
) {
    val sizePx = with(LocalDensity.current) { size.toPx() }
    Icon(
        imageVector = icon,
        contentDescription = null, // Decorative
        tint = color,
        modifier = Modifier
            .size(size)
            .offset {
                IntOffset(
                    (position.x - sizePx / 2).roundToInt(),
                    (position.y - sizePx / 2).roundToInt()
                )
            }
    )
}

@Composable
private fun SpeedLegend(
    minSpeed: Double,
    maxSpeed: Double,
    slowColor: Color,
    fastColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Text("${minSpeed.roundToInt()} km/h", style = MaterialTheme.typography.bodySmall, color = Color.Black)
        Text(
            stringResource(R.string.feature_trips_map_legend_slow),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black
        )
        Spacer(Modifier.width(4.dp))
        Box(
            Modifier
                .size(width = 50.dp, height = 4.dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(slowColor, fastColor)),
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Spacer(Modifier.width(4.dp))
        Text(
            stringResource(R.string.feature_trips_map_legend_fast),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black
        )
        //Text("${maxSpeed.roundToInt()} km/h", style = MaterialTheme.typography.bodySmall, color = Color.Black)
    }
}

@Composable
private fun DistanceScale(
    minLat: Double,
    minLng: Double,
    maxLat: Double,
    maxLng: Double,
    viewWidthPx: Float,
    insetPx: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val midLat = (minLat + maxLat) / 2
    val boxWidthMeters = haversineMeters(midLat, minLng, midLat, maxLng)
    if (boxWidthMeters == 0.0) return

    val metersPerPixel = boxWidthMeters / (viewWidthPx - 2 * insetPx)
    val targetScaleWidthPx = viewWidthPx * 0.20f
    val targetMeters = targetScaleWidthPx * metersPerPixel
    val niceMeters = niceDistance(targetMeters)
    val scalePx = (niceMeters / metersPerPixel).toFloat()
    val scaleDp = with(density) { scalePx.toDp() }
    val scaleLabel =
        if (niceMeters >= 1000) "%.1f km".format(niceMeters / 1000) else "${niceMeters.toInt()} m"

    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .height(2.dp)
                .width(scaleDp)
                .background(Color.Black)
        )
        Spacer(Modifier.width(4.dp))
        Text(scaleLabel, style = MaterialTheme.typography.bodySmall, color = Color.Black)
    }
}


// --- Math & Data ---

fun haversineMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val r = 6_371_000.0 // Earth's radius in meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a =
        sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(
            2
        )
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

private fun niceDistance(meters: Double): Double {
    if (meters <= 0) return 0.0
    val exp = floor(log10(meters))
    val base = 10.0.pow(exp)
    val d = meters / base
    return base * when {
        d < 1.5 -> 1.0
        d < 3.0 -> 2.0
        d < 7.0 -> 5.0
        else -> 10.0
    }
}
