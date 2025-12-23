package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BikeConnectionButton(onConnectClick: () -> Unit, isConnected: Boolean) {
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
            text = if (isConnected) "Disconnect (Sim)" else "Connect (Sim)"
        )
    }
}