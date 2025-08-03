package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun BigBikeProgressIndicator(
    // SIGNATURE REMAINS THE SAME
    uiState: BikeUiState.Success,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 8.dp,
    iconSize: Dp = 48.dp,
    iconTint: Color = Color.Gray,
    containerHeight: Dp = 70.dp,
    onBikeClick: () -> Unit
) {

    // EXTRACT DATA FROM THE STATE OBJECT
    val bikeData = uiState.bikeData
    val currentDistance = bikeData.currentTripDistance
    val totalDistance = bikeData.totalTripDistance
    val lastUpdateTime = bikeData.lastGpsUpdateTime // USE THE RELIABLE TIMESTAMP

    // ANIMATION LOGIC TO PULSE THE ICON
    var animatedTint by remember { mutableStateOf(iconTint) }
    // USE lastUpdateTime AS THE TRIGGER
    LaunchedEffect(lastUpdateTime) {
        // Don't flash on the initial composition (when timestamp is 0)
        if (lastUpdateTime > 0L) {
            animatedTint = Color.Blue // Flash to Blue
            delay(250) // Hold the flash
            animatedTint = iconTint // Return to the original color
        }
    }
    val finalTint by animateColorAsState(
        targetValue = animatedTint,
        animationSpec = tween(durationMillis = 500),
        label = "IconPulse"
    )

    // Compute raw fraction if we have a totalDistance
    val rawFraction: Float? = totalDistance
        ?.takeIf { it > 0f }
        ?.let { (currentDistance / it).coerceIn(0f, 1f) }

    // Decide target fraction: center (0.5) before set, else rawFraction
    val targetFraction = rawFraction ?: 0.5f

    // Animate any change in fraction
    val fraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "ProgressFraction"
    )

    // Before user sets a distance, just show a centered tappable bike — no track
    if (rawFraction == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(containerHeight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
                contentDescription = "Set total distance",
                tint = finalTint, // USE ANIMATED TINT
                modifier = Modifier
                    .size(iconSize)
                    .clickable { onBikeClick() }
            )
        }
        return
    }

    // Once totalDistance is non-null, draw track, markers, progress, and bike
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(containerHeight)
    ) {
        val bc = this
        val dens = LocalDensity.current
        val widthPx = with(dens) { bc.maxWidth.toPx() }
        val heightPx = with(dens) { bc.maxHeight.toPx() }
        val trackHeightPx = with(dens) { trackHeight.toPx() }
        val iconSizePx = with(dens) { iconSize.toPx() }

        val lineY = heightPx / 2f
        val paddingX = iconSizePx / 2f
        val leftX = paddingX
        val rightX = widthPx - paddingX
        val trackWidthPx = rightX - leftX

        val bikeCenterX = leftX + trackWidthPx * fraction
        val bikeTopY = lineY - iconSizePx / 2f

        // Draw the background track + progress
        Canvas(Modifier.matchParentSize()) {
            // full gray track
            drawLine(
                color = Color.LightGray,
                start = Offset(leftX, lineY),
                end = Offset(rightX, lineY),
                strokeWidth = trackHeightPx
            )
            // colored progress bar
            drawLine(
                color = Color(0xFF90CAF9),
                start = Offset(leftX, lineY),
                end = Offset(leftX + trackWidthPx * fraction, lineY),
                strokeWidth = trackHeightPx
            )
        }

        // Distance markers: 0 / mid / full
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = iconSize / 2)
                .offset(y = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0 km", style = MaterialTheme.typography.bodySmall)
            totalDistance?.let {
                Text((it / 2).displayKm(), style = MaterialTheme.typography.bodySmall)
                Text(it.displayKm(), style = MaterialTheme.typography.bodySmall)
            }
        }

        // The bike icon, positioned along (and clickable)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
            contentDescription = "Trip Progress",
            tint = finalTint, // USE ANIMATED TINT
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset {
                    IntOffset(
                        x = (bikeCenterX - iconSizePx / 2f).roundToInt(),
                        y = bikeTopY.roundToInt()
                    )
                }
                .size(iconSize)
                .clickable { onBikeClick() }
        )
    }
}

private fun Float.displayKm() = if (this % 1 == 0f) {
    "${this.toInt()} km"
} else {
    String.format("%.1f km", this)
}

// --- Previews Updated to build a sample UiState ---
private fun createFakeSuccessState(
    currentDistance: Float,
    totalDistance: Float?,
    location: LatLng? = null,
    lastGpsUpdateTime: Long = 0L // ADD TIMESTAMP TO PREVIEW HELPER
): BikeUiState.Success {
    return BikeUiState.Success(
        bikeData = BikeRideInfo(
            location = location,
            currentSpeed = 0.0,
            averageSpeed = 0.0,
            maxSpeed = 0.0,
            currentTripDistance = currentDistance,
            totalTripDistance = totalDistance,
            remainingDistance = totalDistance?.let { it - currentDistance },
            elevationGain = 0.0,
            elevationLoss = 0.0,
            caloriesBurned = 0,
            rideDuration = "",
            settings = persistentMapOf(),
            heading = 0f,
            elevation = 0.0,
            isBikeConnected = false,
            heartbeat = null,
            batteryLevel = null,
            motorPower = null,
            rideState = RideState.NotStarted,
            bikeWeatherInfo = null,
            lastGpsUpdateTime = lastGpsUpdateTime // PASS TIMESTAMP
        )
    )
}

@Preview(showBackground = true)
@Composable
fun BigBikeProgressIndicatorPreview() {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1) initial state
        BigBikeProgressIndicator(
            uiState = createFakeSuccessState(currentDistance = 0f, totalDistance = null),
            iconTint = Color.DarkGray,
            onBikeClick = { }
        )
        Spacer(Modifier.height(24.dp))

        // 2) with a 10 km plan, at 2.5 km ridden
        BigBikeProgressIndicator(
            uiState = createFakeSuccessState(
                currentDistance = 2.5f, // Assuming distance in km
                totalDistance = 10f,
                location = LatLng(0.0, 0.0),
                lastGpsUpdateTime = 1L // Provide a non-zero timestamp for preview
            ),
            iconTint = Color(0xFF4CAF50),
            onBikeClick = { }
        )
    }
}
