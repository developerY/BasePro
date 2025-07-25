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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.twotone.AddCircle
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.ylabz.basepro.core.model.ble.ScanState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StatusBar(
    permissionState: MultiplePermissionsState,
    onManagePermissionsClick: () -> Unit,
    scanState: ScanState // Add scanState parameter
) {
    var isExpanded by remember { mutableStateOf(false) } // Track the expanded state

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Header Row: Shows the title and the toggle icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BLE.Status",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // Scan state
                // Scan state with icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (scanState) {
                            ScanState.NOT_SCANNING -> Icons.Outlined.PlayArrow
                            ScanState.SCANNING -> Icons.Filled.PlayArrow
                            ScanState.STOPPING -> Icons.Outlined.Clear
                        },
                        contentDescription = when (scanState) {
                            ScanState.NOT_SCANNING -> "Not Scanning"
                            ScanState.SCANNING -> "Scanning"
                            ScanState.STOPPING -> "Stopping"
                        },
                        tint = when (scanState) {
                            ScanState.NOT_SCANNING -> Color.Red //MaterialTheme.colorScheme.error
                            ScanState.SCANNING -> Color.Green //MaterialTheme.colorScheme.secondary
                            ScanState.STOPPING -> Color.Yellow // MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (scanState) {
                            ScanState.NOT_SCANNING -> "Not Scanning"
                            ScanState.SCANNING -> "Scanning..."
                            ScanState.STOPPING -> "Stopping..."
                        },
                        color = //MaterialTheme.colorScheme.onPrimary,
                            when (scanState) {
                                ScanState.NOT_SCANNING -> Color(0xFFE91E63) //MaterialTheme.colorScheme.error
                                ScanState.SCANNING -> Color(0xFF009688) //MaterialTheme.colorScheme.secondary
                                ScanState.STOPPING -> Color.Yellow // MaterialTheme.colorScheme.primary
                            },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }







                IconButton(
                    onClick = { isExpanded = !isExpanded }, // Toggle expand/collapse
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Content: List permissions when expanded
            if (isExpanded) {
                permissionState.permissions.forEach { permission ->
                    val (friendlyName, icon, color) = when {
                    !permission.status.isGranted && permission.status.shouldShowRationale -> Triple(
                        getFriendlyName(permission.permission),
                        Icons.Default.Info, // Not Yet Requested
                        Color.Yellow
                    )
                    !permission.status.isGranted -> Triple(
                        getFriendlyName(permission.permission),
                        Icons.Default.Warning, // Permanently Denied
                        Color.Red
                    )
                    permission.status.isGranted -> Triple(
                        getFriendlyName(permission.permission),
                        Icons.Default.CheckCircle, // Granted
                        Color.Green
                    )
                    else -> Triple(
                        getFriendlyName(permission.permission),
                        Icons.Default.Info,
                        Color.Yellow // Default fallback to yellow
                    )
                }


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = friendlyName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // Manage Permissions Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { onManagePermissionsClick() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Manage Permissions",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Legend for colors
                Legend()
            }
        }
    }
}

@Composable
fun Legend() {
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

    Column {
        Text(
            text = "Legend:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Granted", style = MaterialTheme.typography.bodySmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Permanently Denied", style = MaterialTheme.typography.bodySmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Not Yet Requested", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun getFriendlyName(permission: String): String {
    return when (permission) {
        android.Manifest.permission.BLUETOOTH_SCAN -> "Scan Nearby"
        android.Manifest.permission.BLUETOOTH_CONNECT -> "Connect"
        android.Manifest.permission.BLUETOOTH_ADVERTISE -> "Advertise"
        android.Manifest.permission.ACCESS_COARSE_LOCATION -> "Coarse Location"
        android.Manifest.permission.ACCESS_FINE_LOCATION -> "Fine Location"
        else -> "Unknown"
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true)
@Composable
fun StatusBarPreview() {
    // Mock permissions state for preview
    val permissions = listOf(
       android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val permissionState = rememberMultiplePermissionsState(permissions) {
        /* Handle permission requests */
    }

    StatusBar(
        permissionState = permissionState,
        onManagePermissionsClick = { /* Mock click */ },
        scanState = ScanState.SCANNING // Example scan state
    )
}



