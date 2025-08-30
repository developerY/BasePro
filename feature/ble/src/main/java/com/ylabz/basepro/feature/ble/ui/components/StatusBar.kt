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
// //import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.ylabz.basepro.core.model.ble.ScanState
import androidx.compose.ui.res.stringResource // Added import
import com.ylabz.basepro.feature.ble.R // Added import
import com.ylabz.basepro.core.ui.R as CoreUiR // Ensured this import

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
                    text = stringResource(id = R.string.ble_status_bar_title), // Changed
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
                            ScanState.NOT_SCANNING -> stringResource(id = R.string.ble_cd_scan_state_not_scanning)
                            ScanState.SCANNING -> stringResource(id = R.string.ble_cd_scan_state_scanning)
                            ScanState.STOPPING -> stringResource(id = R.string.ble_cd_scan_state_stopping)
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
                            ScanState.NOT_SCANNING -> stringResource(id = R.string.ble_text_scan_state_not_scanning)
                            ScanState.SCANNING -> stringResource(id = R.string.ble_text_scan_state_scanning)
                            ScanState.STOPPING -> stringResource(id = R.string.ble_text_scan_state_stopping)
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
                        contentDescription = if (isExpanded) stringResource(id = CoreUiR.string.action_collapse) else stringResource(id = CoreUiR.string.action_expand),                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Content: List permissions when expanded
            if (isExpanded) {
                permissionState.permissions.forEach { permission ->
                    Row( // This Row is a @Composable context
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        // Determine icon and color first (these are not composable calls)
                        val (icon, color) = when {
                            !permission.status.isGranted && permission.status.shouldShowRationale ->
                                Icons.Default.Info to Color.Yellow // Not Yet Requested
                            !permission.status.isGranted ->
                                Icons.Default.Warning to Color.Red // Permanently Denied
                            permission.status.isGranted ->
                                Icons.Default.CheckCircle to Color.Green // Granted
                            else ->
                                Icons.Default.Info to Color.Yellow // Default fallback
                        }

                        // Now, call getFriendlyName within this composable scope
                        val friendlyNameText = getFriendlyName(permission.permission)

                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = friendlyNameText, // Use the result of the @Composable call
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
                            contentDescription = stringResource(id = R.string.ble_status_bar_cd_manage_permissions), // Changed
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
            text = stringResource(id = R.string.ble_status_bar_legend_title), // Changed
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(id = R.string.ble_status_bar_legend_granted), style = MaterialTheme.typography.bodySmall) // Changed
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(id = R.string.ble_status_bar_legend_denied), style = MaterialTheme.typography.bodySmall) // Changed
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(id = R.string.ble_status_bar_legend_not_requested), style = MaterialTheme.typography.bodySmall) // Changed
        }
    }
}

@Composable
private fun getFriendlyName(permission: String): String {
    return when (permission) {
        android.Manifest.permission.BLUETOOTH_SCAN -> stringResource(id = R.string.ble_status_bar_perm_scan_nearby) // Changed
        android.Manifest.permission.BLUETOOTH_CONNECT -> stringResource(id = CoreUiR.string.action_connect)        
        android.Manifest.permission.BLUETOOTH_ADVERTISE -> stringResource(id = R.string.ble_status_bar_perm_advertise) // Changed
        android.Manifest.permission.ACCESS_COARSE_LOCATION -> stringResource(id = R.string.ble_status_bar_perm_coarse_location) // Changed
        android.Manifest.permission.ACCESS_FINE_LOCATION -> stringResource(id = R.string.ble_status_bar_perm_fine_location) // Changed
        else -> stringResource(id = R.string.ble_status_bar_perm_unknown) // Changed
    }
}

/*
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
*/


