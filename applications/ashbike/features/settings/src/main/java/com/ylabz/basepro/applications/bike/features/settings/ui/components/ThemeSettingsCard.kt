package com.ylabz.basepro.applications.bike.features.settings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ThemeSettingsCard(
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    currentTheme: String,
    onThemeSelected: (String) -> Unit
) {
    val themeOptions = listOf("System", "Light", "Dark")
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable { onExpandToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.BrightnessMedium, contentDescription = "Theme")
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Theme", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = currentTheme, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            if (expanded) {
                HorizontalDivider()
                Column(modifier = Modifier.padding(16.dp)) {
                    themeOptions.forEach { theme ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (theme == currentTheme),
                                    onClick = { onThemeSelected(theme) }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (theme == currentTheme),
                                onClick = { onThemeSelected(theme) }
                            )
                            Text(
                                text = theme,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ThemeSettingsCardPreview() {
    var expanded by remember { mutableStateOf(true) }
    var currentTheme by remember { mutableStateOf("System") }

    ThemeSettingsCard(
        expanded = expanded,
        onExpandToggle = { expanded = !expanded },
        currentTheme = currentTheme,
        onThemeSelected = { theme ->
            currentTheme = theme
            // Optionally, you might want to collapse the card after selection
            // expanded = false
        }
    )
}