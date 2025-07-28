package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.Pause // Not directly used after string resources
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.R // Assuming this is your R file
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.ui.R as CoreUiR // Added import


@Composable
fun BikePathWithControls(
    modifier: Modifier = Modifier,
    getRideState: () -> RideState,
    getCurrentTripDistance: () -> Float,
    getTotalTripDistance: () -> Float?,
    onBikeEvent: (BikeEvent) -> Unit,
    iconSize: Dp = 48.dp,
    trackHeight: Dp = 8.dp,
    buttonSize: Dp = 60.dp
) {
    var showDistanceDialog by remember { mutableStateOf(false) }

    val rideState = getRideState()

    // Determine the FAB icon & description based on rideState
    val fabIcon = when (rideState) {
        RideState.Riding -> Icons.Default.PedalBike
        RideState.NotStarted,
        RideState.Ended -> Icons.Default.PlayArrow
    }
    val fabDesc = when (rideState) {
        RideState.Riding -> stringResource(CoreUiR.string.action_pause)
        RideState.NotStarted,
        RideState.Ended -> stringResource(CoreUiR.string.action_start)
    }

    val currentDistance = getCurrentTripDistance()
    val totalDistance = getTotalTripDistance()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(buttonSize)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FloatingActionButton(
            onClick = { onBikeEvent(BikeEvent.StartRide) },
            containerColor = if (rideState == RideState.Riding) Color.Gray else Color.White,
            contentColor = Color.Black,
            modifier = Modifier.size(buttonSize)
        ) {
            Icon(imageVector = fabIcon, contentDescription = fabDesc)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            BigBikeProgressIndicator(
                currentDistance = currentDistance,
                totalDistance = totalDistance,
                trackHeight = trackHeight,
                iconSize = iconSize,
                iconTint = if (rideState == RideState.Riding) Color(0xFF4CAF50) else Color.LightGray,
                containerHeight = buttonSize,
                onBikeClick = { showDistanceDialog = true }
            )
        }
        FloatingActionButton(
            onClick = { onBikeEvent(BikeEvent.StopRide) },
            containerColor = if (rideState == RideState.Riding) Color.White else Color.LightGray,
            contentColor = Color.Red,
            modifier = Modifier.size(buttonSize)
        ) {
            Icon(imageVector = Icons.Default.Stop, contentDescription = stringResource(CoreUiR.string.action_stop))
        }
    }

    if (showDistanceDialog) {
        var text by remember { mutableStateOf("") }
        LaunchedEffect(getTotalTripDistance()) { // Keyed to the result of the lambda
            text = getTotalTripDistance()?.toString() ?: ""
        }

        AlertDialog(
            onDismissRequest = { showDistanceDialog = false },
            title = { Text(stringResource(R.string.bike_dialog_set_distance_title)) },
            text = {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.bike_dialog_set_distance_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    text.toFloatOrNull()?.let { entered ->
                        onBikeEvent(BikeEvent.SetTotalDistance(entered))
                    }
                    showDistanceDialog = false
                }) {
                    Text(stringResource(CoreUiR.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDistanceDialog = false
                }) {
                    Text(stringResource(CoreUiR.string.action_cancel))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BikePathWithControlsPreview() {
    val demoInfo = BikeRideInfo(
        location = LatLng(37.42, -122.08),
        currentSpeed = 0.0,
        averageSpeed = 0.0,
        maxSpeed = 0.0,
        currentTripDistance = 10.0f, // Example value
        totalTripDistance = 25.0f,   // Example value
        remainingDistance = null,
        elevationGain = 0.0,
        elevationLoss = 0.0,
        caloriesBurned = 0,
        rideDuration = "00:00",
        settings = emptyMap(),
        heading = 0f,
        elevation = 0.0,
        isBikeConnected = false,
        batteryLevel = null,
        motorPower = null,
        rideState = RideState.Riding, // Example state
        bikeWeatherInfo = null
    )
    BikePathWithControls(
        getRideState = { demoInfo.rideState },
        getCurrentTripDistance = { demoInfo.currentTripDistance },
        getTotalTripDistance = { demoInfo.totalTripDistance },
        onBikeEvent = {}
    )
}


/*fun BikePathWithControlsPreview() {
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
}*/
