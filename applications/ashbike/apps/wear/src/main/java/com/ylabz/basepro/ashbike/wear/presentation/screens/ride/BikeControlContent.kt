package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.ylabz.basepro.ashbike.wear.presentation.components.WearSpeedometer
import com.ylabz.basepro.core.model.bike.BikeRideInfo

// 3. Stateless UI
@Composable
fun BikeControlContent(
    rideInfo: BikeRideInfo,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Background Speedometer
        WearSpeedometer(
            // BikeRideInfo provides speed in km/h automatically from BikeForegroundService
            currentSpeed = rideInfo.currentSpeed.toFloat(),
            modifier = Modifier.fillMaxSize()
        )

        // Overlay Stats
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top: Heart Rate
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 30.dp)) {
                Text(
                    text = "HR",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    // Handle nullable heartbeat
                    text = "${rideInfo.heartbeat ?: "--"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Bottom: Distance & Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Text(
                    // BikeRideInfo.currentTripDistance is already in Kilometers
                    text = String.format("%.2f km", rideInfo.currentTripDistance),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Go")
                    }
                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.filledTonalButtonColors(),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Stop")
                    }
                }
            }
        }
    }
}