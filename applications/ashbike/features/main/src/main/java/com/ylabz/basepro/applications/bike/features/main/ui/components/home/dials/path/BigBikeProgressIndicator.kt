package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import kotlin.math.roundToInt

@Composable
fun BigBikeProgressIndicator(
    modifier: Modifier = Modifier,
    // 1. SIGNATURE CHANGED TO ACCEPT UI STATE
    uiState: BikeUiState.Success,
    onEvent: (BikeEvent) -> Unit,
    trackHeight: Dp = 8.dp,
    iconSize: Dp = 48.dp,
    iconTint: Color = Color.Gray,
    containerHeight: Dp = 70.dp,
) {

    val bikeData = uiState.bikeData // Access bikeData from uiState
    val currentDistance = bikeData.currentTripDistance // Updated
    val totalDistance = bikeData.totalTripDistance // Updated

    // --- STABLE LAMBDA CREATION ---
    val onBikeClick = remember { { onEvent(BikeEvent.OnBikeClick) } }

    // 1) Compute raw fraction if we have a totalDistance
    val rawFraction: Float? = totalDistance
        ?.takeIf { it > 0f }
        ?.let { (currentDistance / it).coerceIn(0f, 1f) }

    // 2) Decide target fraction: center (0.5) before set, else rawFraction
    val targetFraction = rawFraction ?: 0.5f

    // 3) Animate any change in fraction
    val fraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
    )

    // 4) Before user sets a distance, just show a centered tappable bike — no track
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
                tint = iconTint,
                modifier = Modifier
                    .size(iconSize)
                    .clickable { onBikeClick() }
            )
        }
        return
    }

    // 5) Once totalDistance is non-null, draw track, markers, progress, and bike
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
            Text("0 km", style = MaterialTheme.typography.bodySmall)
            Text((totalDistance / 2).displayKm(), style = MaterialTheme.typography.bodySmall)
            Text(totalDistance.displayKm(), style = MaterialTheme.typography.bodySmall)
        }

        // The bike icon, positioned along (and clickable)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
            contentDescription = "Trip Progress",
            tint = iconTint,
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
    "${this.toInt()} km"
} else {
    "${"%.1f".format(this)} km"
}

/*
@Preview
@Composable
private fun BigBikeProgressIndicatorPreview() {
    BigBikeProgressIndicator(
        uiState = BikeUiState.Success(
            bikeData = BikeRideInfo(
                location = null,
                currentSpeed = 0.0,
                averageSpeed = 0.0,
                maxSpeed = 0.0,
                currentTripDistance = 25f,
                totalTripDistance = 100f,
                remainingDistance = 75f,
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
                motorPower = null
            )
        ),
        onEvent = {}
    )
}
*/