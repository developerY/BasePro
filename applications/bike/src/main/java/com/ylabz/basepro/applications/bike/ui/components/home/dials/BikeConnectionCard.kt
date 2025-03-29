package com.ylabz.basepro.applications.bike.ui.components.home.dials

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SegmentedBatteryIndicator(
    batteryLevel: Int,
    modifier: Modifier = Modifier
) {
    val segments = 10
    // Calculate how many segments to fill: battery level is from 0 to 100.
    val filledSegments = batteryLevel / (100 / segments)
    // Choose color based on battery level.
    // Define a list of 10 colors representing battery levels from low (red) to high (green)
    val batteryColors = listOf(
        Color(0xFFB71C1C), // 0-10%
        Color(0xFFFF5722), // 11-20%
        Color(0xFFFF9800), // 21-30%
        Color(0xFFFFC107), // 31-40%
        Color(0xFFD7DC39), // 41-50%
        Color(0xFF6F8C41), // 51-60%
        Color(0xFF698C41), // 61-70%
        Color(0xFF409343), // 71-80%
        Color(0xFF37853B), // 81-90%
        Color(0xFF2A6E2D)  // 91-100%
    )

// Compute the index based on battery level (ensuring it doesn't exceed the list bounds)
    val index = (batteryLevel / 10).coerceAtMost(9)
    val segmentColor = batteryColors[index]


    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(segments) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .background(
                        color = if (index < filledSegments) segmentColor else Color.LightGray,
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
    }
}

@Composable
fun BikeConnectionCard(
    isConnected: Boolean,
    batteryLevel: Int?, // Battery percentage when connected, null if not connected
    onConnectClick: () -> Unit
) {
    // Choose colors based on connection state
    val backgroundColor = if (isConnected) Color(0xFF74FFA5) else Color(0xFF2196F3)
    val cardText = if (isConnected) "Battery: ${batteryLevel ?: 0}%" else "Tap to Connect Bike"
    // Choose icon based on connection state
    val connectionIcon = if (isConnected) Icons.Filled.BluetoothConnected else Icons.Filled.BluetoothDisabled

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onConnectClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Row with icon and text.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = connectionIcon,
                    contentDescription = if (isConnected) "Connected to Bike" else "Not Connected",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = cardText,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            // If connected, show the segmented battery indicator.
            if (isConnected && batteryLevel != null) {
                Spacer(modifier = Modifier.height(8.dp))
                SegmentedBatteryIndicator(
                    batteryLevel = batteryLevel,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BikeConnectionCardLoopPreview() {
    val batteryLevels = listOf(10,20,30,40,50,70) //listOf(10,20,30,40,50,70,80,90,100)
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        batteryLevels.forEach { level ->
            BikeConnectionCard(
                isConnected = true,
                batteryLevel = level,
                onConnectClick = {}
            )
        }
    }
}
