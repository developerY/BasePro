package com.ylabz.basepro.feature.ble.ui.components

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
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.ble.DeviceCharacteristic
import com.ylabz.basepro.core.model.ble.DeviceService

@Composable
fun GattServicesList(
    modifier: Modifier = Modifier,
    services: List<DeviceService>,
    readBat: () -> Unit
) {
    Column(modifier = modifier.padding(16.dp)) {
        Button(
            onClick = readBat,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Read Battery Level")
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(services) { service ->
                ExpandableGattServiceCard(service)
            }
        }
    }
}

@Composable
fun ExpandableGattServiceCard(service: DeviceService) {
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

@Preview(showBackground = true)
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
