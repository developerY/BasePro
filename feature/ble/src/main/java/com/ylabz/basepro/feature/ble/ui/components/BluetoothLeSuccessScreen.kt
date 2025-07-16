package com.ylabz.basepro.feature.ble.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    isStartScanningEnabled: Boolean,
    startScan: () -> Unit,
    stopScan: () -> Unit,
    connectToActiveDevice: () -> Unit,
    readCharacteristics: () -> Unit,
    gattServicesList: List<DeviceService>,
    onDeviceSelected: (BluetoothDeviceInfo) -> Unit,
) {
    var isDeviceListExpanded by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .height(500.dp) // Temporarily give it a fixed height
            .padding(horizontal = 16.dp), // Apply horizontal padding once
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            ScanControls(
                scanState = scanState,
                isStartScanningEnabled = isStartScanningEnabled,
                startScan = startScan,
                stopScan = stopScan
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
                                    enabled = scanState != ScanState.SCANNING // Simplified condition
                                ) {
                                    Text("Connect")
                                }
                            }
                            if (gattConnectionState == GattConnectionState.Connected) {
                                Button(onClick = { readCharacteristics() }) {
                                    Text("Read Values")
                                }
                            }
                        }
                        Text(
                            text = "Status: ${gattConnectionState}",//.name.replace("_", " ")}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(10.dp)) }
            item { HorizontalDivider() }
            item { Spacer(modifier = Modifier.height(10.dp)) }
        }

        // Section for displaying discovered devices (always active now)
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
                    imageVector = if (isDeviceListExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = if (isDeviceListExpanded) "Collapse" else "Expand"
                )
            }
            HorizontalDivider()
        }

        if (isDeviceListExpanded) {
            val sortedTotalDevices = discoveredDevices.sortedByDescending { it.rssi }
            val sortedNamedDevices = sortedTotalDevices.filter { device ->
                val name = device.name
                name != null && name.isNotBlank() && !name.equals("Unknown Device", ignoreCase = true)
            }

            if (discoveredDevices.isEmpty()) {
                item {
                    Text(
                        text = if (scanState == ScanState.SCANNING) "Scanning for devices..." else "No devices found.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (sortedNamedDevices.isEmpty()) {
                item {
                    Text(
                        text = "No devices with usable names found. (Total scanned: ${discoveredDevices.size})",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(sortedNamedDevices, key = { it.address }) { device ->
                    DiscoveredDeviceItem(device = device, onDeviceSelected = onDeviceSelected)
                    HorizontalDivider()
                }
            }
        }
        item { Spacer(modifier = Modifier.height(10.dp)) }


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
            } else if (activeDevice != null) {
                item {
                    Text(
                        "No services discovered or available for this device.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun DiscoveredDeviceItem(
    device: BluetoothDeviceInfo,
    onDeviceSelected: (BluetoothDeviceInfo) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDeviceSelected(device) }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            // This will display "Unknown Device" if name is null/blank,
            // but we've already filtered out "Unknown Device" names for the list itself.
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

@Preview(showBackground = true, name = "Screen Preview - Devices Found")
@Composable
fun BluetoothLeSuccessScreenPreview_DevicesFound() {
    MaterialTheme {
        BluetoothLeSuccessScreen(
            scanState = ScanState.SCANNING, // Or ScanState.NOT_SCANNING
            gattConnectionState = GattConnectionState.Disconnected,
            activeDevice = null,
            discoveredDevices = listOf(
                BluetoothDeviceInfo(name = "Device A (Close)", address = "AA:BB:CC:DD:EE:FF", rssi = -50),
                BluetoothDeviceInfo(name = "Device B (Far)", address = "11:22:33:44:55:66", rssi = -85),
                BluetoothDeviceInfo(name = "null", address = "77:88:99:AA:BB:CC", rssi = -40),
                BluetoothDeviceInfo(name = "Unknown Device", address = "DD:EE:FF:00:11:22", rssi = -80)
            ),
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

@Preview(showBackground = true, name = "Screen Preview - No Devices")
@Composable
fun BluetoothLeSuccessScreenPreview_NoDevices() {
    MaterialTheme {
        BluetoothLeSuccessScreen(
            scanState = ScanState.NOT_SCANNING,
            gattConnectionState = GattConnectionState.Disconnected,
            activeDevice = null,
            discoveredDevices = emptyList(),
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

@Preview(showBackground = true, name = "Screen Preview - Active Device Connected")
@Composable
fun BluetoothLeSuccessScreenPreview_ActiveDeviceConnected() {
    MaterialTheme {
        BluetoothLeSuccessScreen(
            scanState = ScanState.NOT_SCANNING,
            gattConnectionState = GattConnectionState.Connected,
            activeDevice = BluetoothDeviceInfo(
                name = "My BLE Device",
                address = "00:11:22:33:44:55",
                rssi = -60
            ),
            discoveredDevices = listOf( /* Can be empty or have other devices */ ),
            isStartScanningEnabled = true,
            startScan = {},
            stopScan = {},
            connectToActiveDevice = {},
            readCharacteristics = {},
            onDeviceSelected = {},
            gattServicesList = listOf(
                DeviceService(
                    uuid = "f000aa00",
                    characteristics = listOf(
                        DeviceCharacteristic(
                            uuid = "f000aa01",
                            value = "0x01",
                            name = TODO(),
                            isReadable = TODO(),
                            isWritable = TODO(),
                            isNotifiable = TODO()
                        )
                    ),
                    name ="null"
                ),

            )
        )
    }
}
