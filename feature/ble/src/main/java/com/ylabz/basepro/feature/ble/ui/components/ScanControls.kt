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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.ble.ScanState
import com.ylabz.basepro.feature.ble.R

@Composable
fun ScanControls(
    scanState: ScanState,
    isStartScanningEnabled: Boolean,
    startScan: () -> Unit,
    stopScan: () -> Unit,
    modifier: Modifier = Modifier
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
                enabled = isStartScanningEnabled && scanState != ScanState.SCANNING
            ) {
                Text(stringResource(id = R.string.ble_scan_controls_start_scan))
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = stopScan,
                enabled = scanState == ScanState.SCANNING
            ) {
                Text(stringResource(id = R.string.ble_scan_controls_stop_scan))
            }
        }

        Spacer(Modifier.height(8.dp)) // Keep this spacer for visual separation

        val statusText = when (scanState) {
            ScanState.NOT_SCANNING -> stringResource(id = R.string.ble_scan_controls_status_not_scanning)
            ScanState.SCANNING -> stringResource(id = R.string.ble_scan_controls_status_scanning)
            ScanState.STOPPING -> stringResource(id = R.string.ble_scan_controls_status_stopping)
        }
        Text(
            text = stringResource(id = R.string.ble_scan_controls_status_label) + " " + statusText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
