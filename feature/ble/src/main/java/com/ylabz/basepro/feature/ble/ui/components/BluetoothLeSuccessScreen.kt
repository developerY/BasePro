package com.ylabz.basepro.feature.ble.ui.components

import android.R.attr.fontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import com.ylabz.basepro.core.model.ble.GattCharacteristicValue
import com.ylabz.basepro.core.model.ble.GattConnectionState
import com.ylabz.basepro.core.model.ble.ScanState
import com.ylabz.basepro.feature.ble.ui.BluetoothLeEvent
import com.ylabz.basepro.feature.ble.ui.BluetoothLeEvent.GattCharacteristicList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BluetoothLeSuccessScreen(
    scanState: ScanState,
    gattConnectionState: GattConnectionState,
    device: BluetoothDeviceInfo?,
    isStartScanningEnabled: Boolean,
    startScan: () -> Unit,
    stopScan: () -> Unit,
    connectToDevice: () -> Unit,
    readCharacteristics: () -> Unit,
    gattServicesList: List<DeviceService>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header Section
        if (device != null && device.name.contains("CC2650 SensorTag", ignoreCase = true)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
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
                            text = "TI Tag Sensor Found!",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "RSSI: ${device.rssi} dBm",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    if (gattConnectionState == GattConnectionState.Disconnected) {
                        Button(
                            onClick = connectToDevice,
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
        } else {
            Text(
                text = if (scanState == ScanState.NOT_SCANNING) "Idle" else "Searching for TI Tag Sensor...",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Device Info Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            if (device == null) {
                Text(
                    text = "No devices found. Try (re)scanning.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            } else {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${device.name} (${device.address})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // GATT Services List Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "GATT Services:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        GattServicesList(
                            services = gattServicesList,
                        )
                    }
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
                Text("Start Scan")
            }

            Button(
                onClick = stopScan,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Stop Scan")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BluetoothLeSuccessScreenPreview() {
    BluetoothLeSuccessScreen(
        scanState = ScanState.SCANNING,
        gattConnectionState = GattConnectionState.Disconnected,
        device = BluetoothDeviceInfo(
            name = "CC2650 SensorTag",
            address = "00:11:22:33:44:55",
            rssi = -70
        ),
        isStartScanningEnabled = true,
        startScan = { println("Start scanning...") },
        stopScan = { println("Stop scanning...") },
        connectToDevice = { println("Connecting to device...") },
        readCharacteristics = { println("Reading characteristics from device ...") },
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
                    value = "CC2650 Sensor"
                )
            )
        )

        )
    )
}

