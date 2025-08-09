package com.ylabz.basepro.applications.bike.features.settings.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import com.ylabz.basepro.applications.bike.database.ProfileData
// import androidx.compose.foundation.clickable // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.foundation.layout.Box // Not directly used after removing ProfileBikeInfoCardEx
import androidx.compose.foundation.layout.Column // Still used by SettingsScreenExPreview
import androidx.compose.foundation.layout.Row // Still used by SettingsScreenExPreview (indirectly if ProfileInfoCardEx uses it)
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.foundation.layout.fillMaxWidth // Not directly used after removing ProfileBikeInfoCardEx
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
// import androidx.compose.foundation.layout.size // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.foundation.layout.width // Not directly used after removing ProfileBikeInfoCardEx
import androidx.compose.foundation.lazy.LazyColumn
// import androidx.compose.material.icons.Icons // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.material.icons.filled.Person // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.material3.Card // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.material3.CardDefaults // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.material3.Icon // Not directly used after removing ProfileBikeInfoCardEx
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment // Still used by SettingsScreenExPreview (indirectly)
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
// import androidx.compose.ui.text.font.FontWeight // Not directly used after removing ProfileBikeInfoCardEx
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.settings.R
import com.ylabz.basepro.applications.bike.features.settings.ui.components.AboutExpandable
import com.ylabz.basepro.applications.bike.features.settings.ui.components.AppPreferencesExpandable
import com.ylabz.basepro.applications.bike.features.settings.ui.components.BLEExpandableCard
import com.ylabz.basepro.applications.bike.features.settings.ui.components.BikeConfigurationEx
import com.ylabz.basepro.applications.bike.features.settings.ui.components.NfcExpandableEx
import com.ylabz.basepro.applications.bike.features.settings.ui.components.ProfileInfoCardEx // Assuming this is the correct one
import com.ylabz.basepro.applications.bike.features.settings.ui.components.QrExpandableEx
import com.ylabz.basepro.applications.bike.features.settings.ui.components.SectionHeader
import com.ylabz.basepro.applications.bike.features.settings.ui.components.ThemeSettingsCard
import com.ylabz.basepro.applications.bike.features.settings.ui.components.health.HealthExpandableEx
import com.ylabz.basepro.core.model.bike.LocationEnergyLevel
import com.ylabz.basepro.core.ui.theme.AshBikeTheme
import com.ylabz.basepro.feature.ble.ui.BluetoothLeEvent
import com.ylabz.basepro.feature.ble.ui.BluetoothLeUiState
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState

internal object AppPreferenceKeys {
    const val KEY_THEME = "Theme"
    const val KEY_NOTIFICATIONS = "Notifications"
    const val KEY_UNITS = "Units"

    const val VALUE_THEME_DARK = "Dark"
    const val VALUE_THEME_LIGHT = "Light"
    const val VALUE_THEME_SYSTEM = "System"
    const val VALUE_NOTIFICATIONS_ENABLED = "Enabled"
    const val VALUE_NOTIFICATIONS_DISABLED = "Disabled"
    const val VALUE_UNITS_METRIC = "Metric (SI)"
    const val VALUE_UNITS_IMPERIAL = "Imperial (English)"
}

private enum class SectionKey { App, Connectivity, Bike }
private enum class CardKey { Theme, About, Health, Nfc, Qr, Ble, BikeConfig, AppPrefs }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsScreenEx(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState.Success,
    onEvent: (SettingsEvent) -> Unit,
    nfcUiState: NfcUiState,
    nfcEvent: (NfcRwEvent) -> Unit,
    bleUiState: BluetoothLeUiState,
    bleEvent: (BluetoothLeEvent) -> Unit,
    navTo: (String) -> Unit
) {
    val expandedSections = remember { mutableStateSetOf<SectionKey>() }
    val expandedCards = remember { mutableStateSetOf<CardKey>() }
    var isEditing by remember { mutableStateOf(false) }

    fun <T> toggle(set: MutableSet<T>, key: T) {
        if (set.contains(key)) set.remove(key) else set.add(key)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        item {
            uiState.profile?.let {
                ProfileInfoCardEx(
                    profile = it,
                    isEditing = isEditing,
                    onToggleEdit = { isEditing = !isEditing },
                    onEvent = onEvent,
                    isProfileIncomplete = uiState.isProfileIncomplete
                )
            } ?: run {
                Text(
                    stringResource(R.string.settings_profile_data_unavailable),
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        stickyHeader {
            SectionHeader(
                title = stringResource(R.string.settings_section_title_app),
                expanded = expandedSections.contains(SectionKey.App),
                onToggle = { toggle(expandedSections, SectionKey.App) }
            )
        }
        if (expandedSections.contains(SectionKey.App)) {
            item {
                ThemeSettingsCard(
                    title = stringResource(R.string.settings_card_title_theme),
                    expanded = expandedCards.contains(CardKey.Theme),
                    onExpandToggle = { toggle(expandedCards, CardKey.Theme) },
                    currentTheme = uiState.selections[AppPreferenceKeys.KEY_THEME] ?: AppPreferenceKeys.VALUE_THEME_SYSTEM,
                    onThemeSelected = { theme -> onEvent(SettingsEvent.UpdateSetting(AppPreferenceKeys.KEY_THEME, theme)) }
                )
            }
            item {
                AppPreferencesExpandable(
                    expanded = expandedCards.contains(CardKey.AppPrefs),
                    onExpandToggle = { toggle(expandedCards, CardKey.AppPrefs) },
                    uiState = uiState,
                    onEvent = onEvent
                )
            }
            item {
                AboutExpandable(
                    expanded = expandedCards.contains(CardKey.About),
                    onExpandToggle = { toggle(expandedCards, CardKey.About) }
                )
            }
        }

        stickyHeader {
            SectionHeader(
                title = stringResource(R.string.settings_section_title_connectivity),
                expanded = expandedSections.contains(SectionKey.Connectivity),
                onToggle = { toggle(expandedSections, SectionKey.Connectivity) }
            )
        }
        if (expandedSections.contains(SectionKey.Connectivity)) {
            item {
                HealthExpandableEx(
                    expanded = expandedCards.contains(CardKey.Health),
                    onExpandToggle = { toggle(expandedCards, CardKey.Health) },
                    navTo = navTo
                )
            }
            item {
                NfcExpandableEx(
                    nfcUiState = nfcUiState,
                    nfcEvent = nfcEvent,
                    expanded = expandedCards.contains(CardKey.Nfc),
                    onExpandToggle = { toggle(expandedCards, CardKey.Nfc) },
                    navTo = navTo
                )
            }
            item {
                QrExpandableEx(
                    expanded = expandedCards.contains(CardKey.Qr),
                    onExpandToggle = { toggle(expandedCards, CardKey.Qr) }
                )
            }
            item {
                BLEExpandableCard(
                    expanded = expandedCards.contains(CardKey.Ble),
                    onExpandToggle = { toggle(expandedCards, CardKey.Ble) }
                )
            }
        }

        stickyHeader {
            SectionHeader(
                title = stringResource(R.string.settings_section_title_bike),
                expanded = expandedSections.contains(SectionKey.Bike),
                onToggle = { toggle(expandedSections, SectionKey.Bike) }
            )
        }
        if (expandedSections.contains(SectionKey.Bike)) {
            item {
                BikeConfigurationEx(
                    expanded = expandedCards.contains(CardKey.BikeConfig),
                    onExpandToggle = { toggle(expandedCards, CardKey.BikeConfig) },
                    navTo = navTo
                )
            }
        }
    }
}

// ProfileBikeInfoCardEx has been removed.

@Preview(showBackground = true)
@Composable
fun SettingsScreenExPreview() {
    val dummyProfile = ProfileData(name = "John Doe", heightCm = "180", weightKg = "80")
    val dummyUiState = SettingsUiState.Success(
        options = mapOf(
            AppPreferenceKeys.KEY_THEME to listOf(AppPreferenceKeys.VALUE_THEME_LIGHT, AppPreferenceKeys.VALUE_THEME_DARK, AppPreferenceKeys.VALUE_THEME_SYSTEM),
            "Language" to listOf("English", "Spanish", "French"),
            AppPreferenceKeys.KEY_NOTIFICATIONS to listOf(AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED, AppPreferenceKeys.VALUE_NOTIFICATIONS_DISABLED),
            AppPreferenceKeys.KEY_UNITS to listOf(AppPreferenceKeys.VALUE_UNITS_IMPERIAL, AppPreferenceKeys.VALUE_UNITS_METRIC)
        ),
        selections = mapOf(
            AppPreferenceKeys.KEY_THEME to AppPreferenceKeys.VALUE_THEME_SYSTEM,
            "Language" to "English",
            AppPreferenceKeys.KEY_NOTIFICATIONS to AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED,
            AppPreferenceKeys.KEY_UNITS to AppPreferenceKeys.VALUE_UNITS_METRIC
        ),
        profile = dummyProfile,
        isProfileIncomplete = false,
        currentEnergyLevel = LocationEnergyLevel.BALANCED
    )
    AshBikeTheme {
        SettingsScreenEx(
            uiState = dummyUiState, onEvent = { }, navTo = { },
            nfcUiState = NfcUiState.Stopped, nfcEvent = { },
            bleUiState = BluetoothLeUiState.Loading, bleEvent = { },
        )
    }
}
