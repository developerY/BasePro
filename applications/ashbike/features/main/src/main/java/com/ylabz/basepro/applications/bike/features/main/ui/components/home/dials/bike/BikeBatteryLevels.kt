package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
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

    if (isConnected) connectedColor else disconnectedColor
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    // Change color: Green for "Connect", Red/Gray for "Disconnect"
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConnected) Color(0xFFFFCDD2) else Color(0xFFC8E6C9), // Light Red vs Light Green
                        contentColor = if (isConnected) Color(0xFFC62828) else Color(0xFF2E7D32)   // Dark Red vs Dark Green text
                    )
                ) {
                    // Change Icon and Text based on state
                    Icon(
                        imageVector = if (isConnected) Icons.Default.LinkOff else Icons.Default.Link,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isConnected) "Disconnect Bike (Sim)" else "Tap to Connect Bike (Sim)"
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