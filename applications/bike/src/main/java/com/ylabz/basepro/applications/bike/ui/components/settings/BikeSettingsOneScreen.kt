package com.ylabz.basepro.applications.bike.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.applications.bike.ui.BikeEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeSettingsOneScreen(
    settings: Map<String, List<String>>,
    onEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                settings.forEach { (settingKey, options) ->
                    SettingRow(
                        settingKey = settingKey,
                        options = options,
                        onSettingSelected = { selectedOption ->
                            // Dispatch an event to update this setting.
                            onEvent(BikeEvent.UpdateSetting(settingKey, selectedOption))
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    )
}

@Composable
fun SettingRow(
    settingKey: String,
    options: List<String>,
    onSettingSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Keep track of the currently selected option locally.
    var selectedOption by remember { mutableStateOf(options.firstOrNull() ?: "") }
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = settingKey,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                OutlinedButton(
                    onClick = {
                        selectedOption = option
                        onSettingSelected(option)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (option == selectedOption) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent
                    )
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BikeSettingsScreenPreview() {
    val sampleSettings = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
    )
    MaterialTheme {
        BikeSettingsOneScreen(
            settings = sampleSettings,
            onEvent = {},
            navTo = {}
        )
    }
}
