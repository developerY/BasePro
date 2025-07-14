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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    onScanAllDevicesChanged: (Boolean) -> Unit,
    isStartScanningEnabled: Boolean,
    startScan: () -> Unit,
    stopScan: () -> Unit,
    connectToActiveDevice: () -> Unit, // Renamed for clarity
    readCharacteristics: () -> Unit,
    onDeviceSelected: (BluetoothDeviceInfo) -> Unit, // Callback for when a device is selected from the list
    gattServicesList: List<DeviceService>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Scan Mode Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Scan for all devices:")
            Switch(
                checked = scanAllDevices,
                onCheckedChange = onScanAllDevicesChanged
            )
        }

        HorizontalDivider()

        // Header Section - Connection to Active Device
        // This section is primarily relevant when an activeDevice is set (either by SensorTag scan or selection)
        if (activeDevice != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = activeDevice.name ?: "Unnamed Device",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "RSSI: ${activeDevice.rssi} dBm",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    if (gattConnectionState == GattConnectionState.Disconnected) {
                        Button(
                            onClick = connectToActiveDevice,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Connect")
                        }
                    } else if (gattConnectionState == GattConnectionState.Connected) {
                        Button(
                            onClick = readCharacteristics,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Read Values")
                        }
                    }
                }
            }
        } else if (!scanAllDevices && scanState == ScanState.SCANNING) {
            Text(
                text = "Searching for TI Tag Sensor...",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Content: Discovered Devices List or Active Device Details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            if (scanAllDevices) {
                if (scanState == ScanState.SCANNING && discoveredDevices.isEmpty()) {
                    Text(
                        text = "Scanning for all devices...",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (discoveredDevices.isEmpty()) {
                    Text(
                        text = "No devices found. Try scanning.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(discoveredDevices, key = { it.address }) { device ->
                            DiscoveredDeviceItem(device = device, onDeviceSelected = onDeviceSelected)
                            HorizontalDivider()
                        }
                    }
                }
            } else {
                // Show details of the activeDevice if not in scanAllDevices mode
                if (activeDevice != null) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${activeDevice.name ?: "N/A"} (${activeDevice.address})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            // GATT Services List Section for activeDevice
                            if (gattConnectionState == GattConnectionState.Connected) {
                                Text(
                                    text = "GATT Services:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                GattServicesList(services = gattServicesList)
                            } else {
                                Text("Connect to device to see services.")
                            }
                        }
                    }
                } else if (scanState != ScanState.SCANNING) {
                    Text(
                        text = "No TI Tag Sensor found. Try scanning or switch to scan all devices.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    )
                }
            }
        }

        // Scan Control Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = startScan,
                enabled = isStartScanningEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isStartScanningEnabled) MaterialTheme.colorScheme.primary else Color.Gray
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (scanState == ScanState.SCANNING) "Scanning..." else "Start Scan")
            }

            Button(
                onClick = stopScan,
                enabled = scanState == ScanState.SCANNING,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Stop Scan")
            }
        }
    }
}

@Composable
fun DiscoveredDeviceItem(
    device: BluetoothDeviceInfo,
    onDeviceSelected: (BluetoothDeviceInfo) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onDeviceSelected(device) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name ?: "Unnamed Device",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = device.address,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "RSSI: ${device.rssi}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true, name = "Scanning All Devices")
@Composable
fun BluetoothLeSuccessScreenPreview_ScanAll() {
    MaterialTheme {
        BluetoothLeSuccessScreen(
            scanState = ScanState.SCANNING,
            gattConnectionState = GattConnectionState.Disconnected,
            activeDevice = null,
            discoveredDevices = listOf(
                BluetoothDeviceInfo(name = "Device A", address = "AA:BB:CC:DD:EE:FF", rssi = -50),
                BluetoothDeviceInfo(name = "Device B", address = "11:22:33:44:55:66", rssi = -65)
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

@Preview(showBackground = true, name = "SensorTag Mode - Found & Connected")
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
            discoveredDevices = listOf( BluetoothDeviceInfo(
                name = "CC2650 SensorTag",
                address = "00:11:22:33:44:55",
                rssi = -70
            )), // Should ideally be just the activeDevice here
            scanAllDevices = false,
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
                            isReadable = true,
                            isWritable = false,
                            isNotifiable = true,
                            value = "90%"
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
