package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike

//import androidx.compose.ui.tooling.preview.Preview
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.LaunchGlassButton

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@Composable
fun BikeCard(
    uiState: BikeUiState.Success,
    onBikeEvent: (BikeEvent) -> Unit,
    isConnected: Boolean,
    batteryLevel: Int?,
) {
    // Visuals based on state
    val containerColor = if (isConnected)
        Color(0xFF4CAF50) // Green (Connected)
    else
        MaterialTheme.colorScheme.surfaceVariant // Gray (Disconnected)

    val contentColor = if (isConnected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    val statusText = if (isConnected) {
        "Battery: ${batteryLevel ?: 0}%"
    } else {
        "No Bike Connected" // Passive text, not a call to action
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        // REMOVED: .clickable {} — The card is now read-only for the Bike part
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. BIKE STATUS (Read-Only)
            Text(
                text = statusText,
                style = MaterialTheme.typography.titleMedium
            )

            if (isConnected && batteryLevel != null) {
                Spacer(Modifier.height(8.dp))
                // Your existing battery indicator
                SegmentedBatteryIndicator(batteryLevel = batteryLevel)
            } else {
                /* Optional: Add a subtle icon indicating "Waiting for signal..."
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "(Looking for BLE Signal...)",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f)
                )*/
            }

            Spacer(Modifier.height(16.dp))

            // 2. GLASS CONTROL (Still Interactive!)
            // This button MUST remain clickable because it launches the Activity
            LaunchGlassButton(
                isGlassSessionActive = uiState.isGlassActive,
                buttonState = uiState.glassButtonState,
                onButtonClick = { onBikeEvent(BikeEvent.ToggleGlassProjection) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
/*
@Preview
@Composable
fun BikeBatteryLevelsPreview() {
    BikeBatteryLevels(
        isConnected = false,
        batteryLevel = 75,
        onConnectClick = {}
    )
}
*/