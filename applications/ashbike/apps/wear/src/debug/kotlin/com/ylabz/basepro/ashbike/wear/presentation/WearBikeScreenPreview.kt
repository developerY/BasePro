package com.ylabz.basepro.ashbike.wear.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material3.MaterialTheme
import com.ylabz.basepro.ashbike.wear.presentation.screens.ride.BikeControlContent
import com.ylabz.basepro.ashbike.wear.presentation.screens.ride.WearBikeUiState // Make sure this is imported
import com.ylabz.basepro.ashbike.wear.presentation.theme.BaseProTheme
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState

@Preview(
    device = "id:wearos_small_round",
    showSystemUi = true,
    name = "Active Ride - Fast",
)
@Preview(
    device = "id:wearos_large_round",
    showSystemUi = true,
    name = "Active Ride - Slow"
)
@Preview(
    device = "id:wearos_xl_round",
    showSystemUi = true,
    name = "Active Ride - XL"
)
@Composable
fun WearBikeScreenPreview() {
    // 1. Create the Mock Data
    val mockRideInfo = BikeRideInfo.initial().copy(
        heartbeat = 145,
        currentSpeed = 25.0,
        currentTripDistance = 12.5F, // Note: usually Double in model
        caloriesBurned = 450,
        rideDuration = "45:30",
        // isBikeConnected = true // (If this field exists in your model)
    )

    // 2. Wrap it in the UI State
    val mockUiState = WearBikeUiState(
        rideInfo = mockRideInfo,
        // Set state to Riding so the "Stop" button appears (simulating isRecording=true)
        rideState = RideState.Riding,
        isServiceBound = true
    )

    BaseProTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            // 3. Use the new signature
            BikeControlContent(
                uiState = mockUiState,
                onEvent = {} // Empty lambda for preview
            )
        }
    }
}

@Preview(
    device = "id:wearos_large_round",
    showSystemUi = true,
    name = "Permission State",
)
@Composable
fun PermissionStatePreview() {
    BaseProTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            PermissionRationaleContent(onRequestPermission = {})
        }
    }
}