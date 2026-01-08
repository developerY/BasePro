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
import com.ylabz.basepro.ashbike.wear.presentation.theme.BaseProTheme
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
    name = "Active Ride - XL"
)
@Composable
fun WearBikeScreenPreview() {
    val mockRideInfo = BikeRideInfo.initial().copy(
        heartbeat = 145,
        currentSpeed = 25.0,
        currentTripDistance = 12.5f,
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
            BikeControlContent(
                rideInfo = mockRideInfo,
                onStart = {},
                onStop = {},
                // ✅ FIX: Added the missing callback required for the History Pager
                onHistoryClick = {}
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