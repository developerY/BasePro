package com.ylabz.basepro.feature.ble.ui.components

////import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.DeviceService
import com.ylabz.basepro.core.model.ble.GattConnectionState
import com.ylabz.basepro.core.model.ble.ScanState
import com.ylabz.basepro.feature.ble.R
import com.ylabz.basepro.core.ui.R as CoreUiR

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
    val unknownDeviceName =
        stringResource(id = CoreUiR.string.text_unknown) // Moved stringResource call here

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
                            // Assuming device.name can be null
                            text = device.name
                                ?: stringResource(id = R.string.ble_text_unnamed_device),
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
                                    enabled = scanState != ScanState.SCANNING
                                ) {
                                    Text(stringResource(id = CoreUiR.string.action_connect))
                                }
                            }
                            if (gattConnectionState == GattConnectionState.Connected) {
                                Button(onClick = { readCharacteristics() }) {
                                    Text(stringResource(id = R.string.ble_action_read_values))
                                }
                            }
                        }
                        Text(
                            text = stringResource(
                                id = R.string.ble_text_status_label,
                                gattConnectionState.toString()
                            ),
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
                    text = stringResource(
                        id = R.string.ble_title_discovered_devices,
                        discoveredDevices.size
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isDeviceListExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = if (isDeviceListExpanded) stringResource(id = CoreUiR.string.action_collapse) else stringResource(
                        id = CoreUiR.string.action_expand
                    )
                )
            }
            HorizontalDivider()
        }

        if (isDeviceListExpanded) {
            val sortedTotalDevices = discoveredDevices.sortedByDescending { it.rssi }
            // Assuming device.name can be null, the filter needs to handle it.
            // The original warning "Condition is always 'true'" for 'name != null' suggests 'name' might have been non-nullable in a previous version or context.
            // If device.name IS nullable, the check is valid. If it's NOT nullable, then 'name.isNotBlank()' is sufficient.
            // For safety and to match typical BLE device name handling where names can be null:
            val sortedNamedDevices = sortedTotalDevices.filter { device ->
                val name = device.name
                // Original logic: name != null && name.isNotBlank() && !name.equals("Unknown Device", ignoreCase = true)
                // If name is String?, then `name != null` is correct.
                // If name is String (non-nullable), then `name != null` is redundant.
                // Let's assume name is String? based on typical BluetoothDeviceInfo models.
                name != null && name.isNotBlank() && !name.equals(
                    unknownDeviceName,
                    ignoreCase = true
                ) // Use the variable
            }

            if (discoveredDevices.isEmpty()) {
                item {
                    Text(
                        text = if (scanState == ScanState.SCANNING) stringResource(id = R.string.ble_text_scanning_for_devices) else stringResource(
                            id = R.string.ble_text_no_devices_found
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (sortedNamedDevices.isEmpty()) {
                item {
                    Text(
                        text = stringResource(
                            id = R.string.ble_text_no_named_devices_found,
                            discoveredDevices.size
                        ),
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
                    stringResource(id = R.string.ble_title_gatt_services),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            if (gattServicesList.isNotEmpty()) {
                items(gattServicesList, key = { it.uuid }) { service ->
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)) {
                        Text(
                            stringResource(id = R.string.ble_text_service_uuid, service.uuid),
                            style = MaterialTheme.typography.bodySmall
                        )
                        service.characteristics.forEach { characteristic ->
                            // Assuming characteristic.value can be null
                            val valueDisplay =
                                characteristic.value ?: stringResource(id = CoreUiR.string.text_na)
                            Text(
                                text = stringResource(
                                    id = R.string.ble_text_characteristic_details,
                                    characteristic.uuid,
                                    valueDisplay
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            } else if (activeDevice != null) { // Only show "no services" if an active device is connected but no services found
                item {
                    Text(
                        stringResource(id = R.string.ble_error_no_services_discovered),
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
            // Assuming device.name can be null.
            // The warning "Unnecessary safe call on a non-null receiver" for `device.name?.takeIf` suggests
            // device.name might be non-nullable in your model. If so, `device.name.takeIf` is fine.
            // If device.name IS nullable, then `device.name?.takeIf` is correct.
            // Let's assume device.name is String?
            text = device.name?.takeIf { it.isNotBlank() }
                ?: stringResource(id = CoreUiR.string.text_unknown),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = R.string.ble_text_device_address, device.address),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(id = R.string.ble_text_device_rssi, device.rssi),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/*
// Preview functions remain unchanged as per previous agreement
@Preview(showBackground = true, name = "Screen Preview - Devices Found")
@Composable
fun BluetoothLeSuccessScreenPreview_DevicesFound() {
    MaterialTheme {
        BluetoothLeSuccessScreen(
            scanState = ScanState.SCANNING,
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
                    uuid = "f000aa00-0000-1000-8000-00805f9b34fb", // Example full UUID
                    characteristics = listOf(
                        DeviceCharacteristic(
                            uuid = "f000aa01-0000-1000-8000-00805f9b34fb", // Example full UUID
                            value = "0x01", // This is likely a byte array or hex string in reality
                            name = "Some Characteristic", // Made name nullable
                            isReadable = true,
                            isWritable = false,
                            isNotifiable = true
                        )
                    ),
                    name = "Example Service" // Made name nullable
                )
            )
        )
    }
}
*/