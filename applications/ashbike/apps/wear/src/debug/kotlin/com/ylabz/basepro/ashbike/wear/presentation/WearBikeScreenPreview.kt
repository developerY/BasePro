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
import com.ylabz.basepro.ashbike.wear.service.ExerciseMetrics

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
    val mockMetrics = ExerciseMetrics(
        heartRate = 145.0,
        speed = 25.0 / 3.6, // ~25 km/h (stored as m/s)
        distance = 12500.0, // 12.5 km
        calories = 450.0
    )

    BaseProTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            BikeControlContent(
                metrics = mockMetrics,
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