package com.ylabz.basepro.ashbike.wear.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material3.MaterialTheme
import com.ylabz.basepro.ashbike.wear.presentation.theme.BaseProTheme
// 1. Import the correct model
import com.ylabz.basepro.core.model.bike.BikeRideInfo

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
    name = "Active Ride - Slow"
)
@Composable
fun WearBikeScreenPreview() {
    // 2. Create mock data using the real BikeRideInfo model
    // Note: BikeRideInfo stores speed in km/h and distance in km, so no conversion math is needed here.
    val mockRideInfo = BikeRideInfo.initial().copy(
        heartbeat = 145,
        currentSpeed = 25.0,        // 25 km/h
        currentTripDistance = 12.5f, // 12.5 km
        caloriesBurned = 450,
        rideDuration = "45:30",
        isBikeConnected = true
    )

    BaseProTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            // 3. Pass mockRideInfo to the 'rideInfo' parameter
            BikeControlContent(
                rideInfo = mockRideInfo,
                onStart = {},
                onStop = {}
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