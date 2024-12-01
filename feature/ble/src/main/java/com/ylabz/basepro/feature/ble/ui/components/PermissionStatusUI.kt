package com.ylabz.basepro.feature.ble.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionStatusUI(permissionState: MultiplePermissionsState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = "Bluetooth Permissions",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                permissionState.permissions.forEach { permission ->
                    val (friendlyName, icon, color, statusText) = when (permission.permission) {
                        android.Manifest.permission.BLUETOOTH_SCAN -> Quad(
                            "Scan Nearby Devices",
                            Icons.Default.Check,
                            if (permission.status.isGranted) Color.Green else Color.Red,
                            if (permission.status.isGranted) "Granted" else "Denied"
                        )
                        android.Manifest.permission.BLUETOOTH_CONNECT -> Quad(
                            "Connect to Devices",
                            Icons.Default.Check,
                            if (permission.status.isGranted) Color.Green else Color.Red,
                            if (permission.status.isGranted) "Granted" else "Denied"
                        )
                        android.Manifest.permission.BLUETOOTH_ADVERTISE -> Quad(
                            "Advertise Device",
                            Icons.Default.Check,
                            if (permission.status.isGranted) Color.Green else Color.Red,
                            if (permission.status.isGranted) "Granted" else "Denied"
                        )
                        else -> Quad(
                            "Unknown Permission",
                            Icons.Default.Warning,
                            Color.Yellow,
                            "Unknown"
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = friendlyName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall,
                            color = color
                        )
                    }
                }
            }
        }
    }
}

// Helper class to hold a tuple of four values
data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
