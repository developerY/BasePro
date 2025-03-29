package com.ylabz.basepro.applications.bike.ui.components.home.dials

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
fun BikeConnectionCard(
    isConnected: Boolean,
    batteryLevel: Int?, // Battery percentage when connected, null if not connected
    onConnectClick: () -> Unit
) {
    // Choose colors based on connection state
    val backgroundColor = if (isConnected) Color(0xFF4CAF50) else Color(0xFF2196F3)
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
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
        }
    }
}


// Preview
@Preview
@Composable
fun BikeConnectionCardPreview() {
    BikeConnectionCard(
        isConnected = true,
        batteryLevel = 80,
        onConnectClick = {},
    )

}
