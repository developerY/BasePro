package com.ylabz.basepro.feature.ble.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothDeviceInfo

// Success screen showing BLE devices
@Composable
fun BluetoothLeSuccessScreen(
    devices: List<BluetoothDeviceInfo>,
    onRescan: () -> Unit // Callback to trigger rescan
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Devices Found:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (devices.isEmpty()) {
            Text(
                text = "No devices found. Try rescanning.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(devices) { device ->
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

        Button(
            onClick = { onRescan() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Rescan")
        }
    }
}