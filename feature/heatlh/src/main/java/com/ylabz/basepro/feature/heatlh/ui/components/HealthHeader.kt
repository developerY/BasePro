package com.ylabz.basepro.feature.heatlh.ui.components

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient

@Composable
fun HealthHeader(
    isHealthConnectAvailable: Boolean,
    backgroundReadGranted: Boolean,
    backgroundReadAvailable: Boolean,
    onPermissionsLaunch: (Set<String>) -> Unit,
    backgroundReadPermissions: Set<String>,
    activity: ComponentActivity?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.LightGray)
    ) {
        Text(
            text = if (isHealthConnectAvailable) "Available" else "Not Available",
            color = if (isHealthConnectAvailable) Color.Green else Color.Red,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        )
        Button(onClick = {
            val settingsIntent = Intent().apply {
                action = HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS
            }
            activity?.startActivity(settingsIntent)
        }) {
            Text("Settings")
        }
        if (!backgroundReadGranted) {
            Button(
                onClick = { onPermissionsLaunch(backgroundReadPermissions) },
                enabled = backgroundReadAvailable,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(if (backgroundReadAvailable) "Request Background Read" else "Not Available")
            }
        }
    }
}
