package com.ylabz.basepro.feature.ble.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.DeviceCharacteristic
import com.ylabz.basepro.core.model.ble.DeviceService
import com.ylabz.basepro.core.model.ble.GattConnectionState
import com.ylabz.basepro.core.model.ble.ScanState

@Composable
fun GattServicesList(
    modifier: Modifier = Modifier,
    services: List<DeviceService>,
    readBat: () -> Unit
) {
    Column(modifier = modifier.padding(16.dp)) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(services) { service ->
                ExpandableGattServiceCard(service, {})
            }
        }
    }
}

@Composable
fun ExpandableGattServiceCard(
    service: DeviceService,
    onUpdateValue: (DeviceCharacteristic) -> Unit // Callback to update the characteristic value
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize() // Smooth expand/collapse animation
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "UUID: ${service.uuid}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Characteristics:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                service.characteristics.forEach { characteristic ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = characteristic.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Value: ${characteristic.value}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = { onUpdateValue(characteristic) },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Update Value")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ExpandableGattServiceCardOrig(service: DeviceService) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp)
        ) {
            Text(
                text = service.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "UUID: ${service.uuid}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Characteristics:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                service.characteristics.forEach { characteristic ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = characteristic.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "Value: ${characteristic.value}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "List")
@Composable
fun GattServicesListPreview() {
    val sampleServices = listOf(
        DeviceService(
            uuid = "0000180f-0000-1000-8000-00805f9b34fb",
            name = "Battery Service",
            characteristics = listOf(
                DeviceCharacteristic(
                    uuid = "00002a19-0000-1000-8000-00805f9b34fb",
                    name = "Battery Level",
                    isReadable = true,
                    isWritable = false,
                    isNotifiable = true,
                    value = "85%"
                )
            )
        ),
        DeviceService(
            uuid = "0000180a-0000-1000-8000-00805f9b34fb",
            name = "Device Information Service",
            characteristics = listOf(
                DeviceCharacteristic(
                    uuid = "00002a24-0000-1000-8000-00805f9b34fb",
                    name = "Model Number",
                    isReadable = true,
                    isWritable = false,
                    isNotifiable = false,
                    value = "CC2650"
                ),
                DeviceCharacteristic(
                    uuid = "00002a26-0000-1000-8000-00805f9b34fb",
                    name = "Firmware Revision",
                    isReadable = true,
                    isWritable = false,
                    isNotifiable = false,
                    value = "1.0.0"
                )
            )
        ),
        DeviceService(
            uuid = "00001810-0000-1000-8000-00805f9b34fb",
            name = "Heart Rate Service",
            characteristics = listOf(
                DeviceCharacteristic(
                    uuid = "00002a37-0000-1000-8000-00805f9b34fb",
                    name = "Heart Rate Measurement",
                    isReadable = true,
                    isWritable = false,
                    isNotifiable = true,
                    value = "72 bpm"
                )
            )
        )
    )

    GattServicesList(
        services = sampleServices,
        readBat = { /* Mock battery read action */ }
    )
}


@Preview(showBackground = true, name = "Card")
@Composable
fun ExpandableGattServiceCardPreview() {
    val mockService = DeviceService(
        uuid = "0000180f-0000-1000-8000-00805f9b34fb",
        name = "Battery Service",
        characteristics = listOf(
            DeviceCharacteristic(
                uuid = "00002a19-0000-1000-8000-00805f9b34fb",
                name = "Battery Level",
                isReadable = true,
                isWritable = true,
                isNotifiable = true,
                value = "85%"
            )
        )
    )

    ExpandableGattServiceCard(
        service = mockService,
        onUpdateValue = { characteristic ->
            println("Updating value for ${characteristic.name}")
        }
    )
}


@Preview(showBackground = true, name = "Gatt Services Preview List", showSystemUi = true)
@Composable
fun GattServicesCombinedPreview() {
    LazyRow(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GattServicesListPreview() // Preview of GattServicesList
        }
        item {
            ExpandableGattServiceCardPreview() // Preview of ExpandableGattServiceCard
        }
        item {
            BluetoothLeSuccessScreenPreview() // Preview of the success screen
        }
    }
}


@Preview(group = "GattServicesList")
@Composable
fun GattServicesListPreview2() {
    GattServicesList(
        services = listOf(
            DeviceService(
                uuid = "0000180f-0000-1000-8000-00805f9b34fb",
                name = "Battery Service",
                characteristics = listOf(
                    DeviceCharacteristic(
                        uuid = "00002a19-0000-1000-8000-00805f9b34fb",
                        name = "Battery Level",
                        isReadable = true,
                        isWritable = false,
                        isNotifiable = true,
                        value = "85%"
                    )
                )
            )
        ),
        readBat = { println("Read battery level") }
    )
}

@Preview(group = "GattServicesList")
@Composable
fun ExpandableGattServiceCardPreview2() {
    ExpandableGattServiceCard(
        service = DeviceService(
            uuid = "0000180f-0000-1000-8000-00805f9b34fb",
            name = "Battery Service",
            characteristics = listOf(
                DeviceCharacteristic(
                    uuid = "00002a19-0000-1000-8000-00805f9b34fb",
                    name = "Battery Level",
                    isReadable = true,
                    isWritable = false,
                    isNotifiable = true,
                    value = "90%"
                )
            )
        ),
        onUpdateValue = { println("Update value") }
    )
}

@Preview(group = "GattServicesList")
@Composable
fun BluetoothLeSuccessScreenPreview2() {
    BluetoothLeSuccessScreen(
        scanState = ScanState.SCANNING,
        gattConnectionState = GattConnectionState.Disconnected,
        device = BluetoothDeviceInfo(
            name = "CC2650 SensorTag",
            address = "00:11:22:33:44:55",
            rssi = -70
        ),
        isStartScanningEnabled = true,
        startScan = { println("Start scanning") },
        stopScan = { println("Stop scanning") },
        connectToDevice = { println("Connect to device") },
        readBattLevel = { println("Read battery level") },
        gattServicesList = listOf(
            DeviceService(
                uuid = "0000180f-0000-1000-8000-00805f9b34fb",
                name = "Battery Service",
                characteristics = listOf(
                    DeviceCharacteristic(
                        uuid = "00002a19-0000-1000-8000-00805f9b34fb",
                        name = "Battery Level",
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

