package com.ylabz.basepro.feature.ble.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.ScanState

// Success screen showing BLE devices
@Composable
fun BluetoothLeSuccessScreen(
    scanState: ScanState,
    device: BluetoothDeviceInfo?,
    isStartScanningEnabled: Boolean,
    startScan: () -> Unit, // Callback to trigger rescan
    stopScan: () -> Unit // Callback to trigger stop scan
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (device !=null && device.name.contains("CC2650 SensorTag", ignoreCase = true)) {
            Text(
                text = "TI Tag Sensor Found!",
                color = Color.Green,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {}) {
                Text("Connect")
            }
        } else {
            Text(
                text = if (scanState == ScanState.NOT_SCANNING) "Idle" else "Searching for TI Tag Sensor...",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row {
            Button(
                onClick = { startScan() },
                enabled = isStartScanningEnabled
            ) {
                Text("start")
            }

            Button(
                onClick = { stopScan() },
            ) {
                Text("stop")
            }
        }

        Text(
            text = "Devices Found:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (device == null) {
            Text(
                text = "No devices found. Try (re)scanning.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else {
                Text(
                        text = "${device.name} (${device.address})",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
