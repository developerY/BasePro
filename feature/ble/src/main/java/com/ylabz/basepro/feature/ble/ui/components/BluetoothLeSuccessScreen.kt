package com.ylabz.basepro.feature.ble.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Success screen showing BLE devices
@Composable
fun BluetoothLeSuccessScreen(devices: List<com.ylabz.basepro.core.data.repository.BluetoothDeviceInfo>) {
    LazyColumn {
        items(devices) { device ->
            Text(text = "${device.name} (${device.address})", modifier = Modifier.padding(16.dp))
        }
    }
}