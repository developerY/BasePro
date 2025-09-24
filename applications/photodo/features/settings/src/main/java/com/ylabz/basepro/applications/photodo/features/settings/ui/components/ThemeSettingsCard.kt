package com.ylabz.basepro.applications.photodo.features.settings.ui.components

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
import androidx.compose.material.icons.filled.Palette // Palette icon for theme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.features.settings.R
import com.ylabz.basepro.core.ui.theme.ThemeIdentifiers // Assuming this path is correct

// Sealed class ThemeOption and themeOptionsList can remain as they are.
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

// Helper to get display string for current theme
@Composable
private fun getCurrentThemeDisplayString(themeIdentifier: String): String {
    val resId = when (themeIdentifier) {
        ThemeIdentifiers.LIGHT -> R.string.theme_light_display
        ThemeIdentifiers.DARK -> R.string.theme_dark_display
        ThemeIdentifiers.SYSTEM -> R.string.theme_system_display
        else -> R.string.theme_system_display // Default
    }
    return stringResource(id = resId)
}

@Composable
fun ThemeSettingsCard(
    modifier: Modifier = Modifier,
    title: String, // Expecting this to be a resolved string
    currentTheme: String, // Identifier like ThemeIdentifiers.SYSTEM
    onThemeSelected: (String) -> Unit // Callback with the identifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val currentThemeDisplay = getCurrentThemeDisplayString(currentTheme)

    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp) // Maintain consistent padding
            .fillMaxWidth()
            .clickable { showDialog = true } // Card click opens dialog
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                text = currentThemeDisplay,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary // Highlight current selection
            )
            // Consider adding a dropdown arrow icon if you prefer:
            // Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select theme")
        }
    }

    if (showDialog) {
        ThemeSelectionDialog(
            currentTheme = currentTheme,
            onThemeSelected = { selectedIdentifier ->
                onThemeSelected(selectedIdentifier)
                showDialog = false
            },
            onDismissRequest = { showDialog = false }
        )
    }
}

@Composable
private fun ThemeSelectionDialog(
    currentTheme: String,
    onThemeSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { Icon(Icons.Default.Palette, contentDescription = null) },
        title = { Text(stringResource(R.string.settings_card_title_theme_selection)) }, // e.g., "Select Theme"
        text = {
            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp)) // Divider above options
            Column {
                themeOptionsList.forEach { themeOption ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (themeOption.identifier == currentTheme),
                                onClick = { onThemeSelected(themeOption.identifier) }
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (themeOption.identifier == currentTheme),
                            // onClick = null // RadioButton is controlled by Row's selectable onClick
                            onClick = { onThemeSelected(themeOption.identifier) } // Direct click on radio also works
                        )
                        Text(
                            text = stringResource(themeOption.displayResId),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dialog_action_cancel)) // Use a general "Cancel"
            }
        }
        // Dismiss button can be omitted if cancel serves the purpose.
    )
}
