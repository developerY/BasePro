package com.ylabz.basepro.applications.bike.ui.components.home.dials

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BikeConnectionCard(
    isConnected: Boolean,
    batteryLevel: Int?, // e.g., battery percentage when connected, null if not
    onConnectClick: () -> Unit
) {
    // Choose colors based on connection state
    val backgroundColor = if (isConnected) Color(0xFF4CAF50) else Color(0xFF2196F3)
    val cardText = if (isConnected) "Battery: ${batteryLevel ?: 0}%" else "Tap to Connect Bike"

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
            Text(
                text = cardText,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

// Preview
@Preview
@Composable
fun BikeConnectionCardPreview() {
    BikeConnectionCard(
        isConnected = false,
        batteryLevel = 80,
        onConnectClick = {},
    )

}
