package com.ylabz.basepro.feature.ble.ui.components

import android.text.format.DateUtils.formatDateTime
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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.ScanState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (device != null && device.name.contains("CC2650 SensorTag", ignoreCase = true)) {
                Text(
                    text = "TI Tag Sensor Found!",
                    color = Color.Green,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = { /* Handle connect action */ },
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("Connect")
                }
            } else {
                Text(
                    text = if (scanState == ScanState.NOT_SCANNING) "Idle" else "Searching for TI Tag Sensor...",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Devices List Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Devices Found:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (device == null) {
                Text(
                    text = "No devices found. Try (re)scanning.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            } else {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${device.name} (${device.address})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "RSSI: ${device.rssi} dBm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (device != null && device.name.contains("CC2650 SensorTag", ignoreCase = true)) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Sensor Tag Details",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                Text(
                                    text = "Battery Level: 80%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Temperature: 22°C",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Humidity: 45%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Last Synced: ${formatDateTime(System.currentTimeMillis())}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = startScan,
                enabled = isStartScanningEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isStartScanningEnabled) MaterialTheme.colorScheme.primary else Color.Gray
                )
            ) {
                Text("Start Scan")
            }

            Button(
                onClick = stopScan,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Stop Scan")
            }
        }
    }
}

fun formatDateTime(timestamp: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
