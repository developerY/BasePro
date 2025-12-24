package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
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

@Composable
fun BikeCard(
    uiState: BikeUiState.Success,
    onBikeEvent: (BikeEvent) -> Unit,
    isConnected: Boolean,
    batteryLevel: Int?,
    onConnectClick: () -> Unit
) {
    // Example colors—adjust to your theme
    val disconnectedColor = Color(0xFF2196F3)
    val connectedColor = Color(0xFF4CAF50)

    val backgroundColor = if (isConnected) connectedColor else disconnectedColor

    val displayText = if (isConnected) {
        "Battery: ${batteryLevel ?: 0}%"
    } else {
        "Tap to Connect Bike"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                if (!isConnected) {
                    onConnectClick()
                }
            },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isConnected && batteryLevel != null) {
                // Show a battery bar or 10-segment indicator
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                // 10-segment example or an interpolated bar
                SegmentedBatteryIndicator(batteryLevel = batteryLevel)
            } else {
                // Show a centered, Material button to connect
                BikeConnectionButton(onConnectClick, isConnected)
            }

            // 2. INSERT THE GLASS BUTTON HERE
            LaunchGlassButton(
                buttonState = uiState.glassButtonState, // Read from UI State
                onButtonClick = { onBikeEvent(BikeEvent.ToggleGlassProjection) },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
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