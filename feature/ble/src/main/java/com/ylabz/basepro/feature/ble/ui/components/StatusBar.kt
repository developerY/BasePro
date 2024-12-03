package com.ylabz.basepro.feature.ble.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StatusBar(
    permissionState: MultiplePermissionsState,
    onManagePermissionsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display each permission with an icon and friendly name
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                permissionState.permissions.forEach { permission ->
                    val (friendlyName, color) = when (permission.permission) {
                        android.Manifest.permission.BLUETOOTH_SCAN -> Pair(
                            "Scan Nearby",
                            if (permission.status.isGranted) Color.Green else Color.Red
                        )
                        android.Manifest.permission.BLUETOOTH_CONNECT -> Pair(
                            "Connect",
                            if (permission.status.isGranted) Color.Green else Color.Red
                        )
                        android.Manifest.permission.BLUETOOTH_ADVERTISE -> Pair(
                            "Advertise",
                            if (permission.status.isGranted) Color.Green else Color.Red
                        )
                        else -> Pair(
                            "Unknown",
                            Color.Yellow
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = friendlyName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // Add an icon for managing BLE permissions
            IconButton(
                onClick = { onManagePermissionsClick() },
                modifier = Modifier.size(24.dp) // Adjust icon size for compactness
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Manage Permissions",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
