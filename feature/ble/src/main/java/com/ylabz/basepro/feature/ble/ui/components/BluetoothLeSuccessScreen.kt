package com.ylabz.basepro.feature.ble.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column // Keep for internal structures like DiscoveredDeviceItem
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn // Main scrolling container
import androidx.compose.foundation.lazy.items // For lists within items if necessary
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.DeviceCharacteristic
import com.ylabz.basepro.core.model.ble.DeviceService
import com.ylabz.basepro.core.model.ble.GattConnectionState
import com.ylabz.basepro.core.model.ble.ScanState

@Composable
fun BluetoothLeSuccessScreen(
    scanState: ScanState,
    gattConnectionState: GattConnectionState,
    activeDevice: BluetoothDeviceInfo?,
    discoveredDevices: List<BluetoothDeviceInfo>,
    scanAllDevices: Boolean,
    isStartScanningEnabled: Boolean,
    startScan: () -> Unit,
    stopScan: () -> Unit,
    connectToActiveDevice: () -> Unit,
    readCharacteristics: () -> Unit,
    gattServicesList: List<DeviceService>,
    onScanAllDevicesChanged: (Boolean) -> Unit,
    onDeviceSelected: (BluetoothDeviceInfo) -> Unit,
) {
    // State for the collapsible discovered devices list
    var isDeviceListExpanded by remember { mutableStateOf(true) }

    LazyColumn( // Main scrolling container for the entire screen
        modifier = Modifier
            .fillMaxSize()
            .height(500.dp) // Temporarily give it a fixed height
            .padding(horizontal = 16.dp), // Apply horizontal padding once
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {"See Here"} // Top padding
        item { Spacer(modifier = Modifier.height(16.dp)) } // Top padding

        item {
            ScanControls(
                scanState = scanState,
                isStartScanningEnabled = isStartScanningEnabled,
                startScan = startScan,
                stopScan = stopScan,
                scanAllDevices = scanAllDevices,
                onScanAllDevicesChanged = onScanAllDevicesChanged
            )
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(10.dp)) }

        activeDevice?.let { device ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = device.name ?: "Unnamed Device",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = device.address,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            if (gattConnectionState == GattConnectionState.Disconnected) {
                                Button(
                                    onClick = { connectToActiveDevice() },
                                    enabled = scanState == ScanState.NOT_SCANNING
                                ) {
                                    Text("Connect")
                                }
                            }
                            if (gattConnectionState == GattConnectionState.Connected) {
                                Button(
                                    onClick = { readCharacteristics() }
                                ) {
                                    Text("Read Values")
                                }
                                // TODO: Add a Disconnect button here
                            }
                        }
                        Text(
                            text = "Status: ${gattConnectionState}", //.name.replace("_", " ")}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(10.dp)) }
            item { HorizontalDivider() }
            item { Spacer(modifier = Modifier.height(10.dp)) }
        }

        // Section for discovered devices OR "not found" message
        if (scanAllDevices) {
            // Header for the discovered devices list
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDeviceListExpanded = !isDeviceListExpanded }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Discovered Devices (${discoveredDevices.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (isDeviceListExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (isDeviceListExpanded) "Collapse" else "Expand"
                    )
                }
                HorizontalDivider()
            }

            // Conditionally add device items or messages
            if (isDeviceListExpanded) {
                if (discoveredDevices.isEmpty()) {
                    item {
                        Text(
                            text = if (scanState == ScanState.SCANNING) "Scanning for devices..." else "No devices found.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(discoveredDevices, key = { it.address }) { device ->
                        DiscoveredDeviceItem(device = device, onDeviceSelected = onDeviceSelected)
                        HorizontalDivider()
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(10.dp)) } // Spacer after the list
        } else {
            // This section is for when scanAllDevices is false
            if (activeDevice == null && scanState == ScanState.NOT_SCANNING) {
                item {
                    Text(
                        "SensorTag not found. Ensure it's discoverable and try scanning.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // GATT Services section
        if (gattConnectionState == GattConnectionState.Connected) {
            item { HorizontalDivider() }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Text(
                    "GATT Services",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            if (gattServicesList.isNotEmpty()) {
                items(gattServicesList, key = { it.uuid }) { service ->
                    // This creates a Column for each service and its characteristics.
                    // This is not a LazyColumn, it will render all services and characteristics
                    // as part of the main LazyColumn's scrollable content.
                    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Text("Service: ${service.uuid}", style = MaterialTheme.typography.bodySmall)
                        service.characteristics.forEach { characteristic ->
                            Text(
                                "  Char: ${characteristic.uuid} - Value: ${characteristic.value ?: "N/A"}",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            } else if (activeDevice != null) { // Connected but no services
                item {
                    Text(
                        "No services discovered or available for this device.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) } // Bottom padding
    }
}

// DiscoveredDeviceItem remains the same
@Composable
fun DiscoveredDeviceItem(
    device: BluetoothDeviceInfo,
    onDeviceSelected: (BluetoothDeviceInfo) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDeviceSelected(device) }
            .padding(horizontal = 16.dp, vertical = 12.dp) // Item-specific padding
    ) {
        Text(
            text = device.name?.takeIf { it.isNotBlank() } ?: "Unknown Device",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Address: ${device.address}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "RSSI: ${device.rssi} dBm",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


// Previews would also need to be adjusted if they were wrapping BluetoothLeSuccessScreen
// in a Column or Box that might conflict, but usually, they are direct.
// For brevity, I'll omit the Preview updates here but they should be reviewed.
// The existing Preview annotations on BluetoothLeSuccessScreen itself might be sufficient if it's the root.

@Preview(showBackground = true, name = "Scanning All Devices - Found & Expanded")
@Composable
fun BluetoothLeSuccessScreenPreview_ScanAllFoundExpanded() {
    MaterialTheme {
        BluetoothLeSuccessScreen(
            scanState = ScanState.SCANNING,
            gattConnectionState = GattConnectionState.Disconnected,
            activeDevice = null,
            discoveredDevices = listOf(
                BluetoothDeviceInfo(name = "Device A", address = "AA:BB:CC:DD:EE:FF", rssi = -50),
                BluetoothDeviceInfo(name = "Device B", address = "11:22:33:44:55:66", rssi = -65),
                BluetoothDeviceInfo(name = "Device C", address = "77:88:99:AA:BB:CC", rssi = -75)
            ),
            scanAllDevices = true,
            onScanAllDevicesChanged = {},
            isStartScanningEnabled = false,
            startScan = {},
            stopScan = {},
            connectToActiveDevice = {},
            readCharacteristics = {},
            onDeviceSelected = {},
            gattServicesList = emptyList()
        )
    }
}

@Preview(showBackground = true, name = "SensorTag Mode - Found & Connected with GATT")
@Composable
fun BluetoothLeSuccessScreenPreview_SensorTagConnected() {
    MaterialTheme {
        BluetoothLeSuccessScreen(
            scanState = ScanState.NOT_SCANNING,
            gattConnectionState = GattConnectionState.Connected,
            activeDevice = BluetoothDeviceInfo(
                name = "CC2650 SensorTag",
                address = "00:11:22:33:44:55",
                rssi = -70
            ),
            discoveredDevices = listOf( BluetoothDeviceInfo( // Usually empty if scanAllDevices is false
                name = "CC2650 SensorTag",
                address = "00:11:22:33:44:55",
                rssi = -70
            )),
            scanAllDevices = false, // SensorTag mode, so not scanning all
            onScanAllDevicesChanged = {},
            isStartScanningEnabled = true,
            startScan = {},
            stopScan = {},
            connectToActiveDevice = {},
            readCharacteristics = {},
            onDeviceSelected = {},
            gattServicesList = listOf(
                DeviceService(
                    uuid = "f000aa00-0451-4000-b000-000000000000", name = "Movement Service", characteristics = listOf(
                        DeviceCharacteristic(
                            uuid = "f000aa01-0451-4000-b000-000000000000", name = "Movement Data",
                            isReadable = true, isWritable = false, isNotifiable = true, value = "Raw: AB-CD-EF"
                        ),
                        DeviceCharacteristic(
                            uuid = "f000aa02-0451-4000-b000-000000000000", name = "Movement Config",
                            isReadable = true, isWritable = true, isNotifiable = false, value = "0x01"
                        )
                    )
                ),
                DeviceService(
                    uuid = "f000ab00-0451-4000-b000-000000000000", name = "Light Service", characteristics = listOf(
                        DeviceCharacteristic(
                            uuid = "f000ab01-0451-4000-b000-000000000000", name = "Light Data",
                            isReadable = true, isWritable = false, isNotifiable = true, value = "500 lux"
                        )
                    )
                )
            )
        )
    }
}

@Preview(showBackground = true, name = "SensorTag Mode - Not Found")
@Composable
fun BluetoothLeSuccessScreenPreview_SensorTagNotFound() {
    MaterialTheme {
        BluetoothLeSuccessScreen(
            scanState = ScanState.NOT_SCANNING,
            gattConnectionState = GattConnectionState.Disconnected,
            activeDevice = null,
            discoveredDevices = emptyList(),
            scanAllDevices = false,
            onScanAllDevicesChanged = {},
            isStartScanningEnabled = true,
            startScan = {},
            stopScan = {},
            connectToActiveDevice = {},
            readCharacteristics = {},
            onDeviceSelected = {},
            gattServicesList = emptyList()
        )
    }
}
