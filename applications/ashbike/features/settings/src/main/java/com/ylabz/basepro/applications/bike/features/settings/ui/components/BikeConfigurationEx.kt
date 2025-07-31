package com.ylabz.basepro.applications.bike.features.settings.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BikeScooter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.settings.R

@Composable
fun BikeConfigurationEx(
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    navTo: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable { onExpandToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.BikeScooter,
                    contentDescription = stringResource(R.string.settings_bike_config_cd),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.settings_bike_config_title),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(if (expanded) R.string.settings_action_collapse else R.string.settings_action_expand)
                )
            }

            if (expanded) {
                HorizontalDivider()
                Column(modifier = Modifier.padding(16.dp)) {
                    var motorAssistance by remember { mutableStateOf(true) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.settings_motor_assistance_label))
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = motorAssistance,
                            enabled = false,
                            onCheckedChange = { motorAssistance = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    var gearingLevel by remember { mutableStateOf(5f) }
                    Text(stringResource(R.string.settings_gearing_level_label, gearingLevel.toInt()))
                    Slider(
                        enabled = false,
                        value = gearingLevel,
                        onValueChange = { gearingLevel = it },
                        valueRange = 1f..10f
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        enabled = false,
                        onClick = { navTo("settings_bike_advanced") }
                    ) {
                        Text(stringResource(R.string.settings_advanced_bike_settings_button))
                    }
                }
            }
        }
    }
}
