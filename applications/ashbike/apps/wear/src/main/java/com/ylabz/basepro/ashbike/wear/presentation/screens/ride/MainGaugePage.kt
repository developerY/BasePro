package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

// --- PAGE 0: Your Existing Gauge UI ---
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.ylabz.basepro.ashbike.wear.presentation.components.WearSpeedometer

@Composable
fun MainGaugePage(
    uiState: WearBikeUiState,
    onEvent: (WearBikeEvent) -> Unit
) {
    val rideInfo = uiState.rideInfo
    val isRecording = uiState.isRecording

    // ... (Animation code from previous answer remains here) ...

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Gauge
        WearSpeedometer(
            currentSpeed = rideInfo.currentSpeed.toFloat(),
            modifier = Modifier.fillMaxSize()
        )

        // Text & Data
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.offset(y = (-10).dp)
        ) {
            // Heart Rate
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${rideInfo.heartbeat ?: "--"}",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            // Speed
            Text(
                text = String.format("%.0f", rideInfo.currentSpeed),
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 54.sp),
                color = MaterialTheme.colorScheme.primary
            )
            Text("km/h", style = MaterialTheme.typography.labelSmall)

            // Distance
            Text(
                text = String.format("%.2f km", rideInfo.currentTripDistance ?: 0.0),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Controls (Trigger Events)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isRecording) {
                // Large Start Button when stopped
                Button(
                    onClick = { onEvent(WearBikeEvent.StartRide) }, // <--- Action
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(Icons.Rounded.PlayArrow, "Start")
                }
            } else {
                // Smaller Stop/Pause buttons when running
                Button(
                    onClick = { onEvent(WearBikeEvent.StopRide) }, // <--- Action
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(Icons.Rounded.Stop, "Stop")
                }
            }
        }
    }
}