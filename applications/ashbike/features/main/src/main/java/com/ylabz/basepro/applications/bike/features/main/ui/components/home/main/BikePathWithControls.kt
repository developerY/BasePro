package com.ylabz.basepro.applications.bike.features.main.ui.components.home.main
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.trips.ui.components.unused.path.BigBikeProgressIndicator
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState


@Composable
fun BikePathWithControls(
    modifier: Modifier = Modifier,
    bikeRideInfo: BikeRideInfo,
    onBikeEvent: (BikeEvent) -> Unit,
    iconSize: Dp = 48.dp,
    trackHeight: Dp = 8.dp,
    buttonSize: Dp = 60.dp
) {
    val currentDistance = bikeRideInfo.currentTripDistance
    val totalDistance = bikeRideInfo.totalTripDistance
    // Riding whenever state == Riding
    val isRiding = bikeRideInfo.rideState == RideState.Riding

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(buttonSize)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Start / Pause button
        FloatingActionButton(
            onClick = { onBikeEvent(BikeEvent.StartPauseRide) },
            containerColor = Color.White,
            contentColor = Color.Black,
            modifier = Modifier.size(buttonSize)
        ) {
            val icon = if (isRiding) Icons.Default.Pause else Icons.Default.PlayArrow
            val desc = if (isRiding) "Pause" else "Start"
            Icon(imageVector = icon, contentDescription = desc)
        }

        // Progress indicator in middle
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            BigBikeProgressIndicator(
                currentDistance = currentDistance,
                totalDistance = totalDistance,
                iconSize = iconSize,
                containerHeight = buttonSize,
                trackHeight = trackHeight
            )
        }

        // Stop (Save) button
        FloatingActionButton(
            onClick = { onBikeEvent(BikeEvent.StopSaveRide) },
            containerColor = Color.White,
            contentColor = Color.Red,
            modifier = Modifier.size(buttonSize)
        ) {
            Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BikePathWithControlsPreview() {
    val demoInfo = BikeRideInfo(
        // Core location & speeds
        location            = LatLng(37.4219999, -122.0862462),
        currentSpeed        = 0.0,
        averageSpeed        = 0.0,
        maxSpeed            = 0.0,

        // Distances (km)
        currentTripDistance = 0.0f,
        totalTripDistance   = null,
        remainingDistance   = null,

        // Elevation (m)
        elevationGain       = 0.0,
        elevationLoss       = 0.0,

        // Calories
        caloriesBurned      = 0,

        // UI state
        rideDuration        = "00:00",
        settings            = mapOf(
            "Theme" to listOf("Light", "Dark", "System Default"),
            "Language" to listOf("English", "Spanish", "French"),
            "Notifications" to listOf("Enabled", "Disabled")
        ),
        heading             = 0f,
        elevation           = 0.0,

        // Bike connectivity
        isBikeConnected     = false,
        batteryLevel        = null,
        motorPower          = null,

        // rideState & weatherInfo use their defaults
    )
    BikePathWithControls(
        bikeRideInfo = demoInfo,
        onBikeEvent = {},
    )
}
