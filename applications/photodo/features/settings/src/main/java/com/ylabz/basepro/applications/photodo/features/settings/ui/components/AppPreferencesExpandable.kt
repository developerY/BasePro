package com.ylabz.basepro.applications.photodo.features.settings.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ylabz.basepro.applications.photodo.features.settings.R
import com.ylabz.basepro.applications.photodo.features.settings.ui.AppPreferenceKeys
import com.ylabz.basepro.applications.photodo.features.settings.ui.SettingsEvent
import com.ylabz.basepro.applications.photodo.features.settings.ui.SettingsUiState

@Composable
fun NotificationSettingsItem( // Renamed and simplified
    modifier: Modifier = Modifier,
    uiState: SettingsUiState.Success,
    onEvent: (SettingsEvent) -> Unit
) {
    val isNotificationsEnabled = uiState.selections[AppPreferenceKeys.KEY_NOTIFICATIONS] == AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED
    
    // Using rememberSaveable to keep the Switch state consistent with the ViewModel's uiState,
    // but allowing immediate visual feedback. The actual source of truth is the ViewModel.
    var notificationsSwitchState by rememberSaveable(isNotificationsEnabled) {
        mutableStateOf(isNotificationsEnabled)
    }

    ListItem(
        modifier = modifier.fillMaxWidth(),
        headlineContent = { Text(stringResource(id = R.string.settings_notifications_label)) },
        leadingContent = {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = stringResource(R.string.settings_notifications_cd_icon) // Add CD
            )
        },
        trailingContent = {
            Switch(
                checked = notificationsSwitchState,
                onCheckedChange = { newCheckedState ->
                    notificationsSwitchState = newCheckedState
                    onEvent(
                        SettingsEvent.UpdateSetting(
                            AppPreferenceKeys.KEY_NOTIFICATIONS,
                            if (newCheckedState) AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED else AppPreferenceKeys.VALUE_NOTIFICATIONS_DISABLED
                        )
                    )
                }
                // Consider if the switch should be enabled/disabled based on some other condition
                // enabled = true
            )
        }
    )
}

// The old AppPreferencesExpandable and its preview can be removed or heavily adapted
// if this file is solely for NotificationSettingsItem now.
// For now, I will comment out the old Preview.
/*
@Preview
@Composable
fun AppPreferencesExpandablePreview() {
    val uiState = SettingsUiState.Success(
        options = mapOf(
            AppPreferenceKeys.KEY_NOTIFICATIONS to listOf(
                AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED,
                AppPreferenceKeys.VALUE_NOTIFICATIONS_DISABLED
            )
        ),
        selections = mapOf(
            AppPreferenceKeys.KEY_NOTIFICATIONS to AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED
        )
    )
    // Assuming a theme is applied higher up in a real scenario
    MaterialTheme {
        NotificationSettingsItem(uiState = uiState, onEvent = {})
    }
}
*/
