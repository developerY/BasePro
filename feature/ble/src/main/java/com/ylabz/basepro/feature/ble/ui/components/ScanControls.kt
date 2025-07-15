package com.ylabz.basepro.feature.ble.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.ble.ScanState // Ensure this import is correct

@Composable
fun ScanControls(
    scanState: ScanState,
    isStartScanningEnabled: Boolean,
    startScan: () -> Unit,
    stopScan: () -> Unit,
    scanAllDevices: Boolean,
    onScanAllDevicesChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier // Allow passing a modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = startScan,
                enabled = isStartScanningEnabled && scanState != ScanState.SCANNING // && scanState != ScanState.BleNotEnabled
            ) {
                Text("Start Scan")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = stopScan,
                enabled = scanState == ScanState.SCANNING
            ) {
                Text("Stop Scan")
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Scan for all devices:",
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = scanAllDevices,
                onCheckedChange = onScanAllDevicesChanged,
                enabled = scanState != ScanState.SCANNING // Disable toggle while scanning
            )
        }

        Text(
            text = "Scan Status: ${scanState.name.replace("_", " ")}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
