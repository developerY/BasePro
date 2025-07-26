package com.ylabz.basepro.feature.ble.ui.components

import android.R.attr.description
import android.text.format.DateUtils.formatDateTime
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ylabz.basepro.core.model.ble.DeviceCharacteristic
import com.ylabz.basepro.core.model.ble.DeviceService
import com.ylabz.basepro.core.model.ble.GattCharacteristicValue
import com.ylabz.basepro.feature.ble.ui.BluetoothLeEvent
import androidx.compose.ui.res.stringResource // Added import
import com.ylabz.basepro.feature.ble.R // Added import


@Composable
fun GattServices(
    modifier: Modifier = Modifier,
    services: List<DeviceService>,
    readBat: () -> Unit
    //onCharacteristicClick: (DeviceService, DeviceCharacteristic) -> Unit // Pass both
) {
    Column {

        Button(
            onClick = readBat
        ) {
            Text(stringResource(id = R.string.ble_action_read_battery_level)) // Updated
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.ble_title_gatt_services), // Updated
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            items(services) { entry: DeviceService ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = entry.name, style = MaterialTheme.typography.bodyMedium)
                        entry.characteristics.forEach { characteristic ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    // Updated with new string R.string.ble_text_characteristic_name
                                    text = stringResource(id = R.string.ble_text_characteristic_name, characteristic.name), 
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                IconButton(onClick = { /*onCharacteristicClick(entry, characteristic)*/ }) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = stringResource(id = R.string.ble_cd_read_characteristic) // Updated
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Gattab(
    modifier: Modifier = Modifier,
    batteryLevel: () -> Int) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.ble_title_sensor_tag_details), // Updated
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                // Updated
                text = stringResource(id = R.string.ble_label_battery_level_formatted, batteryLevel()), 
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                // Updated - Assuming "22°C" is a placeholder for a dynamic value.
                // For now, we format it with the existing placeholder.
                text = stringResource(id = R.string.ble_label_temperature_formatted, "22°C"), 
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                // Updated - Assuming "45%" is a placeholder.
                text = stringResource(id = R.string.ble_label_humidity_formatted, "45%"), 
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                // Updated - Assuming "0" is a placeholder.
                text = stringResource(id = R.string.ble_label_last_synced_formatted, "0"), 
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
