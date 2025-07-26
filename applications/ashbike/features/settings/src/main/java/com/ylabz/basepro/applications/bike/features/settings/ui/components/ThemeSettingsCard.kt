package com.ylabz.basepro.applications.bike.features.settings.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.settings.R // Adjust if your R file is elsewhere
import com.ylabz.basepro.core.ui.theme.ThemeIdentifiers // Import your constants

// Sealed class to represent theme options with their string resource IDs
sealed class ThemeOption(val identifier: String, @StringRes val displayResId: Int) {
    data object SystemTheme : ThemeOption(ThemeIdentifiers.SYSTEM, R.string.theme_system_display)
    data object LightTheme : ThemeOption(ThemeIdentifiers.LIGHT, R.string.theme_light_display)
    data object DarkTheme : ThemeOption(ThemeIdentifiers.DARK, R.string.theme_dark_display)
}

private val themeOptionsList = listOf(
    ThemeOption.SystemTheme,
    ThemeOption.LightTheme,
    ThemeOption.DarkTheme
)

@Composable
fun getCurrentThemeDisplayStringRes(themeIdentifier: String): Int {
    return when (themeIdentifier) {
        ThemeIdentifiers.LIGHT -> R.string.theme_light_display
        ThemeIdentifiers.DARK -> R.string.theme_dark_display
        ThemeIdentifiers.SYSTEM -> R.string.theme_system_display
        else -> R.string.theme_system_display // Default or throw an error
    }
}

@Composable
fun ThemeSettingsCard(
    title: String, // Expecting this to be a resolved string (ideally from stringResource at call site)
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    currentTheme: String, // This will be an identifier like ThemeIdentifiers.SYSTEM
    onThemeSelected: (String) -> Unit // Callback with the identifier
) {
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
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = stringResource(R.string.theme_icon_cd)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(id = getCurrentThemeDisplayStringRes(currentTheme)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(if (expanded) R.string.settings_action_collapse else R.string.settings_action_expand)
                )
            }

            if (expanded) {
                HorizontalDivider()
                Column(modifier = Modifier.padding(16.dp)) {
                    themeOptionsList.forEach { themeOption ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (themeOption.identifier == currentTheme),
                                    onClick = { onThemeSelected(themeOption.identifier) }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (themeOption.identifier == currentTheme),
                                onClick = { onThemeSelected(themeOption.identifier) }
                            )
                            Text(
                                text = stringResource(themeOption.displayResId),
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
    // currentTheme in preview now uses the identifier
    var currentTheme by remember { mutableStateOf(ThemeIdentifiers.SYSTEM) }

    ThemeSettingsCard(
        title = stringResource(R.string.theme_settings_card_title), // Title from string resource
        expanded = expanded,
        onExpandToggle = { expanded = !expanded },
        currentTheme = currentTheme,
        onThemeSelected = { themeIdentifier -> // Receives identifier
            currentTheme = themeIdentifier
        }
    )
}
