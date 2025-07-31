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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.settings.R
import com.ylabz.basepro.applications.bike.features.settings.ui.AppPreferenceKeys // Internal
import com.ylabz.basepro.applications.bike.features.settings.ui.SettingsEvent
import com.ylabz.basepro.applications.bike.features.settings.ui.SettingsUiState
import com.ylabz.basepro.core.model.bike.LocationEnergyLevel

@Composable
fun AppPreferencesExpandable(
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    uiState: SettingsUiState.Success,
    onEvent: (SettingsEvent) -> Unit
) {
    val energyLevelMap = mapOf(
        LocationEnergyLevel.POWER_SAVER to Pair(0f, stringResource(R.string.settings_energy_level_power_saver)),
        LocationEnergyLevel.BALANCED to Pair(1f, stringResource(R.string.settings_energy_level_balanced)),
        LocationEnergyLevel.HIGH_ACCURACY to Pair(2f, stringResource(R.string.settings_energy_level_high_accuracy))
    )

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable { onExpandToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_app_preferences_cd_icon),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(id = R.string.settings_app_preferences_title),
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
                    val currentEnergyLevel = uiState.currentEnergyLevel
                    val currentPair = energyLevelMap[currentEnergyLevel] ?: energyLevelMap[LocationEnergyLevel.BALANCED]!!
                    var sliderValue by remember(currentEnergyLevel) { mutableFloatStateOf(currentPair.first) }
                    val levelLabel = currentPair.second

                    Text(
                        text = stringResource(R.string.settings_location_energy_level_label),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = levelLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Slider(
                        value = sliderValue,
                        onValueChange = { newValue -> sliderValue = newValue },
                        onValueChangeFinished = {
                            val newLevel = energyLevelMap.entries.find { it.value.first == sliderValue }?.key
                            newLevel?.let {
                                //onEvent(SettingsEvent.UpdateEnergyLevel(it)) // Assuming UpdateEnergyLevel event exists
                            }
                        },
                        steps = 1,
                        valueRange = 0f..2f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    var darkModeEnabled by rememberSaveable(uiState.selections[AppPreferenceKeys.KEY_THEME]) {
                        mutableStateOf(uiState.selections[AppPreferenceKeys.KEY_THEME] == AppPreferenceKeys.VALUE_THEME_DARK)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(id = R.string.settings_dark_mode_label))
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = {
                                darkModeEnabled = it
                                val newTheme = if (it) AppPreferenceKeys.VALUE_THEME_DARK else AppPreferenceKeys.VALUE_THEME_LIGHT // Or system default
                                onEvent(SettingsEvent.UpdateSetting(AppPreferenceKeys.KEY_THEME, newTheme))
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    var notificationsEnabled by rememberSaveable(uiState.selections[AppPreferenceKeys.KEY_NOTIFICATIONS]) {
                        mutableStateOf(uiState.selections[AppPreferenceKeys.KEY_NOTIFICATIONS] == AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(id = R.string.settings_notifications_label))
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                notificationsEnabled = it
                                onEvent(SettingsEvent.UpdateSetting(AppPreferenceKeys.KEY_NOTIFICATIONS, if (it) AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED else AppPreferenceKeys.VALUE_NOTIFICATIONS_DISABLED))
                            },
                            enabled = false // As per original code
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    val isMetric = uiState.selections[AppPreferenceKeys.KEY_UNITS] == AppPreferenceKeys.VALUE_UNITS_METRIC
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(id = R.string.settings_units_label) + ":   ")
                        Text(
                            text = if (isMetric) stringResource(id = R.string.settings_units_metric) else stringResource(id = R.string.settings_units_imperial),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = isMetric,
                            onCheckedChange = { newIsMetric ->
                                val newUnit = if (newIsMetric) AppPreferenceKeys.VALUE_UNITS_METRIC else AppPreferenceKeys.VALUE_UNITS_IMPERIAL
                                onEvent(SettingsEvent.UpdateSetting(AppPreferenceKeys.KEY_UNITS, newUnit))
                            },
                            enabled = true
                        )
                    }
                }
            }
        }
    }
}
