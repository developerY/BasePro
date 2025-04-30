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
import androidx.compose.material.icons.filled.Pause
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
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
    // UI-only override for total distance
    //var manualTotal by remember { mutableStateOf(bikeRideInfo.totalTripDistance) }
    var showDistanceDialog by remember { mutableStateOf(false) }

    val rideState = bikeRideInfo.rideState

    // Determine the FAB icon & description based on rideState
    val fabIcon = when (rideState) {
        RideState.Riding  -> Icons.Default.PedalBike
        //RideState.Paused,
        RideState.NotStarted,
        RideState.Ended   -> Icons.Default.PlayArrow
    }
    val fabDesc = when (rideState) {
        RideState.Riding  -> "Pause"
        //RideState.Paused,
        RideState.NotStarted,
        RideState.Ended   -> "Start"
    }


    val currentDistance = bikeRideInfo.currentTripDistance
    var totalDistance   = bikeRideInfo.totalTripDistance


    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(buttonSize)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Start / Pause
        // Start / Pause / Resume button
        /*StartPauseButton(
            rideState = rideState,
            onToggle  = { onBikeEvent(BikeEvent.StartPauseRide) }
        )*/
        FloatingActionButton(
            onClick       = { onBikeEvent(BikeEvent.StartPauseRide) },
            containerColor=  if (rideState == RideState.Riding) Color.Gray else Color.White,
            contentColor  = Color.Black,
            modifier      = Modifier.size(buttonSize)
        ) {
            Icon(imageVector = fabIcon, contentDescription = fabDesc)
        }
        // Bike progress (centered when totalDistance == null)
        Box(
            modifier         = Modifier.weight(1f).fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            BigBikeProgressIndicator(
                currentDistance = currentDistance,
                totalDistance   = totalDistance,
                trackHeight     = trackHeight,
                iconSize        = iconSize,
                iconTint        = if (rideState == RideState.Riding) Color(0xFF4CAF50) else Color.LightGray,
                containerHeight = buttonSize,
                onBikeClick     = { showDistanceDialog = true }
            )
        }

        // Stop & Save
        FloatingActionButton(
            onClick       = { onBikeEvent(BikeEvent.StopSaveRide) },
            containerColor= if (rideState == RideState.Riding) Color.White else Color.LightGray,
            contentColor  = Color.Red,
            modifier      = Modifier.size(buttonSize)
        ) {
            Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop")
        }
    }

    // ——— Distance entry dialog ———
    if (showDistanceDialog) {
        // local text state for the TextField
        var text by remember { mutableStateOf(totalDistance?.toString() ?: "") }

        AlertDialog(
            onDismissRequest = { showDistanceDialog = false },
            title            = { Text("Enter total distance (km)") },
            text             = {
                OutlinedTextField(
                    value            = text,
                    onValueChange    = { text = it },
                    label            = { Text("Distance in km") },
                    keyboardOptions  = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine       = true,
                    modifier         = Modifier.fillMaxWidth()
                )
            },
            confirmButton    = {
                TextButton(onClick = {
                    text.toFloatOrNull()?.let { entered ->
                        totalDistance = entered
                        onBikeEvent(BikeEvent.SetTotalDistance(entered))
                    }
                    showDistanceDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton    = {
                TextButton(onClick = {
                    showDistanceDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BikePathWithControlsPreview() {
    val demoInfo = BikeRideInfo(
        location            = LatLng(37.42, -122.08),
        currentSpeed        = 0.0,
        averageSpeed        = 0.0,
        maxSpeed            = 0.0,
        currentTripDistance = 0.0f,
        totalTripDistance   = null,
        remainingDistance   = null,
        elevationGain       = 0.0,
        elevationLoss       = 0.0,
        caloriesBurned      = 0,
        rideDuration        = "00:00",
        settings            = emptyMap(),
        heading             = 0f,
        elevation           = 0.0,
        isBikeConnected     = false,
        batteryLevel        = null,
        motorPower          = null,
        rideState           = RideState.NotStarted,
        bikeWeatherInfo     = null
    )
    BikePathWithControls(
        bikeRideInfo = demoInfo,
        onBikeEvent  = {}
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
