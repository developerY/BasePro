package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BikeBatteryLevels(
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
        //colors = CardDefaults.cardColors(containerColor = backgroundColor)
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
                    Button(
                        onClick = onConnectClick,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),  // full width
                        shape = RoundedCornerShape(8.dp)     // optional rounding
                    ) {
                        Text(
                            text = displayText,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()  // ensures centered text
                        )
                    }
                }
            }
        }
    }

// Example 10-segment indicator
@Composable
fun SegmentedBatteryIndicator(batteryLevel: Int) {
    val segments = 10
    val filledSegments = (batteryLevel / (100 / segments)).coerceAtMost(10)
    // Color logic (red -> green) or discrete steps
    val fillColor = batteryColor(batteryLevel)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(segments) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = if (index < filledSegments) fillColor else Color.LightGray,
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
    }
}

// Simple color function from red to green
fun batteryColor(batteryLevel: Int): Color {
    val clamped = batteryLevel.coerceIn(0, 100)
    val fraction = clamped / 100f
    return lerp(Color.Red, Color.Green, fraction)
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