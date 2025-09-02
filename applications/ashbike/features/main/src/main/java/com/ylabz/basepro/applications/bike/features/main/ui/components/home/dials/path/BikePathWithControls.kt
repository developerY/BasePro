package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.R
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.ui.R as CoreUiR

@Composable
fun BikePathWithControls(
    modifier: Modifier = Modifier,
    uiState: BikeUiState.Success, // Just pass the state object
    onBikeEvent: (BikeEvent) -> Unit,
    iconSize: Dp = 48.dp,
    trackHeight: Dp = 8.dp,
    buttonSize: Dp = 60.dp
) {

    // --- Stable Lambda Creation ---
    // Create a stable, remembered version of the event handler.
    // This `remember` guarantees that BigBikeProgressIndicator always
    // receives the exact same instance for its onEvent parameter.
    val onBikeEventStable = remember { { event: BikeEvent -> onBikeEvent(event) } }
    val onStartRide = remember { { onBikeEvent(BikeEvent.StartRide) } }
    val onStopRide = remember { { onBikeEvent(BikeEvent.StopRide) } }

    val bikeData = uiState.bikeData // Access bikeData from uiState
    val rideState = bikeData.rideState
    val totalDistance = bikeData.totalTripDistance
    val showDistanceDialog = uiState.showSetDistanceDialog

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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(buttonSize)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FloatingActionButton(
            onClick = onStartRide,
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
                uiState = uiState,
                onEvent = onBikeEventStable,
                trackHeight = trackHeight,
                iconSize = iconSize,
                iconTint = if (rideState == RideState.Riding) Color(0xFF4CAF50) else Color.LightGray,
                containerHeight = buttonSize,
            )
        }
        FloatingActionButton(
            onClick = onStopRide,
            containerColor = if (rideState == RideState.Riding) Color.White else Color.LightGray,
            contentColor = Color.Red,
            modifier = Modifier.size(buttonSize)
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = stringResource(CoreUiR.string.action_stop)
            )
        }
    }

    if (showDistanceDialog) {

        // This 'text' state is ephemeral and local to the dialog, which is fine.
        var text by remember { mutableStateOf("") }

        // When the dialog appears, initialize its text field with the current total distance.
        LaunchedEffect(totalDistance) {
            text = totalDistance?.toString() ?: ""
        }

        // Create stable lambdas for the dialog events
        val onDismissRequest = remember { { onBikeEvent(BikeEvent.DismissSetDistanceDialog) } }

        AlertDialog(
            onDismissRequest = onDismissRequest,
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
                }) {
                    Text(stringResource(CoreUiR.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(CoreUiR.string.action_cancel))
                }
            }
        )
    }
}
/*
@Preview
@Composable
fun BikePathWithControlsPreview() {
    val demoInfo = BikeRideInfo(
        location = LatLng(37.4219999, -122.0862462),
        currentSpeed = 0.0,
        averageSpeed = 0.0,
        maxSpeed = 0.0,
        currentTripDistance = 0.0f,
        totalTripDistance = 10.0f, // Example total distance
        remainingDistance = 10.0f,
        elevationGain = 0.0,
        elevationLoss = 0.0,
        caloriesBurned = 0,
        rideDuration = "00:00",
        settings = persistentMapOf(), // Empty map for simplicity
        heading = 0f,
        elevation = 0.0,
        isBikeConnected = false,
        batteryLevel = null,
        motorPower = null,
        rideState = RideState.NotStarted,
        heartbeat = null,// Example initial state
        lastGpsUpdateTime = 0L
    )
    val uiState = BikeUiState.Success(
        bikeData = demoInfo,
        showSetDistanceDialog = false // Also needed for the preview to work
    )
    BikePathWithControls(uiState = uiState, onBikeEvent = {})
}
*/
