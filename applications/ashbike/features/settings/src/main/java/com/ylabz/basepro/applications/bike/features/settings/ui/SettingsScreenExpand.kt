package com.ylabz.basepro.applications.bike.features.settings.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import com.ylabz.basepro.applications.bike.database.ProfileData
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BikeScooter
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ylabz.basepro.applications.bike.features.settings.R // Fully qualified R class
import com.ylabz.basepro.applications.bike.features.settings.ui.components.ProfileInfoCardEx
// Add import for ThemeSettingsCard
import com.ylabz.basepro.applications.bike.features.settings.ui.components.ThemeSettingsCard
import com.ylabz.basepro.applications.bike.features.settings.ui.components.health.HealthExpandableEx
import com.ylabz.basepro.core.ui.theme.AshBikeTheme
import com.ylabz.basepro.feature.ble.ui.BluetoothLeEvent
import com.ylabz.basepro.feature.ble.ui.BluetoothLeRoute
import com.ylabz.basepro.feature.ble.ui.BluetoothLeUiState
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.NfcUiState.Stopped
import com.ylabz.basepro.feature.nfc.ui.components.NfcScanScreen
import com.ylabz.basepro.feature.qrscanner.ui.QRCodeScannerScreen

// —————————————————————————————————————————————————————————
//  SectionHeader WITH “Collapse All” ACTION
// —————————————————————————————————————————————————————————
// --- Key for managing which card is expanded ---
private enum class SettingsCard {
    THEME, APP, ABOUT, HEALTH, NFC, QR, BLE, BIKE
}

private object AppPreferenceKeys {
    const val KEY_THEME = "Theme"
    const val KEY_NOTIFICATIONS = "Notifications"
    const val KEY_UNITS = "Units"

    const val VALUE_THEME_DARK = "Dark"
    const val VALUE_THEME_LIGHT = "Light"
    const val VALUE_THEME_SYSTEM = "System" // Added
    const val VALUE_NOTIFICATIONS_ENABLED = "Enabled"
    const val VALUE_NOTIFICATIONS_DISABLED = "Disabled"
    const val VALUE_UNITS_METRIC = "Metric (SI)"
    const val VALUE_UNITS_IMPERIAL = "Imperial (English)"
}

// —————————————————————————————————————————————————————————
//  SETTINGS SCREEN (CLEANED UP)
// —————————————————————————————————————————————————————————
// --- Enums for type-safe state management ---
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

    // State sets to track what's open
    val expandedSections = remember { mutableStateSetOf<SectionKey>() }
    val expandedCards = remember { mutableStateSetOf<CardKey>() }
    var isEditing by remember { mutableStateOf(false) }

    // Helper to toggle a key in a set
    fun <T> toggle(set: MutableSet<T>, key: T) {
        if (set.contains(key)) set.remove(key) else set.add(key)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Use theme background
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        // --- Profile Card (Always visible) ---
        item {
            // Check if profile is not null before calling ProfileInfoCardEx
            uiState.profile?.let { nonNullProfile ->
                ProfileInfoCardEx(
                    profile = nonNullProfile,
                    isEditing = isEditing,
                    onToggleEdit = { isEditing = !isEditing },
                    onEvent = onEvent,
                    isProfileIncomplete = uiState.isProfileIncomplete // Pass the flag here
                )
            } ?: run {
                // Optional: What to display if profile is null
                Text(
                    stringResource(R.string.settings_profile_data_unavailable),
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- App Settings Section ---
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
                    currentTheme = uiState.selections[AppPreferenceKeys.KEY_THEME] ?: AppPreferenceKeys.VALUE_THEME_SYSTEM, // Updated
                    onThemeSelected = { theme -> onEvent(SettingsEvent.UpdateSetting(AppPreferenceKeys.KEY_THEME, theme)) } // Updated
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

        // --- Connectivity Section ---
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

        // --- Bike Settings Section ---
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

// Re-usable Section Header composable
@Composable
fun SectionHeader(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            Modifier
                .clickable(onClick = onToggle)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) stringResource(R.string.settings_action_collapse) else stringResource(R.string.settings_action_expand),
            )
        }
    }
}

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
        isProfileIncomplete = false // Added for preview consistency
    )
    AshBikeTheme {
        SettingsScreenEx(
            uiState = dummyUiState, onEvent = { }, navTo = { },
            nfcUiState = Stopped, nfcEvent = { },
            bleUiState = BluetoothLeUiState.Loading, bleEvent = { }
        )
    }
}

// --------------------------------------------
// NFC Expandable
// --------------------------------------------
@Composable
fun NfcExpandableEx(
    nfcUiState: NfcUiState,
    nfcEvent: (NfcRwEvent) -> Unit,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    navTo: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Softer color for less emphasis
    ) {
        Column {
            // Header row (always visible)
            Row(
                modifier = Modifier
                    .clickable { onExpandToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Nfc,
                    contentDescription = stringResource(R.string.settings_nfc_cd),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.settings_nfc_title),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(if (expanded) R.string.settings_action_collapse else R.string.settings_action_expand)
                )
            }

            // Expanded content
            if (expanded) {
                HorizontalDivider()
                Column(modifier = Modifier.padding(16.dp)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    NfcScanScreen(
                        uiState = nfcUiState,
                        onEvent = nfcEvent,
                        navTo = navTo
                    )
                }
            }
        }
    }
}

// --------------------------------------------
// QR Scanner Expandable
// --------------------------------------------
@Composable
fun QrExpandableEx(
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Header row
            Row(
                modifier = Modifier
                    .clickable { onExpandToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = stringResource(R.string.settings_qr_scanner_cd),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.settings_qr_scanner_title),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(if (expanded) R.string.settings_action_collapse else R.string.settings_action_expand)
                )
            }

            // Expanded content
            if (expanded) {
                Divider()
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = stringResource(R.string.settings_qr_scanner_placeholder_text))
                    Spacer(modifier = Modifier.height(8.dp))
                    QRCodeScannerScreen()
                }
            }
        }
    }
}

// --------------------------------------------
// PROFILE / BIKE INFO CARD
// --------------------------------------------
@Composable
fun ProfileBikeInfoCardEx(
    userName: String,
    bikeBattery: String,
    lastRide: String,
    onProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onProfileClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.settings_user_profile_cd),
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.settings_profile_bike_info_details, bikeBattery, lastRide),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// --------------------------------------------
// BIKE CONFIGURATION EXPANDABLE
// --------------------------------------------
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
            // Header row (always visible)
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
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(if (expanded) R.string.settings_action_collapse else R.string.settings_action_expand)
                )
            }

            // Expanded content
            if (expanded) {
                Divider()
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

// --------------------------------------------
// APP PREFERENCES EXPANDABLE
// --------------------------------------------
@Composable
fun AppPreferencesExpandable(
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    uiState: SettingsUiState.Success,
    onEvent: (SettingsEvent) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Header row
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

            // Expanded content
            if (expanded) {
                Divider()
                Column(modifier = Modifier.padding(16.dp)) {
                    // Dark Mode Setting
                    var darkModeEnabled by rememberSaveable { mutableStateOf(uiState.selections[AppPreferenceKeys.KEY_THEME] == AppPreferenceKeys.VALUE_THEME_DARK) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(id = R.string.settings_dark_mode_label))
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = {
                                darkModeEnabled = it
                                val newTheme = if (it) AppPreferenceKeys.VALUE_THEME_DARK else AppPreferenceKeys.VALUE_THEME_LIGHT
                                onEvent(SettingsEvent.UpdateSetting(AppPreferenceKeys.KEY_THEME, newTheme))
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Notifications Setting
                    var notificationsEnabled by rememberSaveable { mutableStateOf(uiState.selections[AppPreferenceKeys.KEY_NOTIFICATIONS] == AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(id = R.string.settings_notifications_label))
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                notificationsEnabled = it
                                onEvent(SettingsEvent.UpdateSetting(AppPreferenceKeys.KEY_NOTIFICATIONS, if (it) AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED else AppPreferenceKeys.VALUE_NOTIFICATIONS_DISABLED))
                            },
                            enabled = false
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Units Setting
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(id = R.string.settings_units_label) + ":   ")
                        Text(
                            text = if (uiState.selections[AppPreferenceKeys.KEY_UNITS] == AppPreferenceKeys.VALUE_UNITS_METRIC) {
                                stringResource(id = R.string.settings_units_metric)
                            } else {
                                stringResource(id = R.string.settings_units_imperial)
                            },
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = uiState.selections[AppPreferenceKeys.KEY_UNITS] == AppPreferenceKeys.VALUE_UNITS_METRIC,
                            onCheckedChange = { isMetric ->
                                val newUnit = if (isMetric) AppPreferenceKeys.VALUE_UNITS_METRIC else AppPreferenceKeys.VALUE_UNITS_IMPERIAL
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

@Composable
fun BLEExpandableCard(
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Header row (always visible)
            Row(
                modifier = Modifier
                    .clickable { onExpandToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Bluetooth, // or any suitable BLE icon
                    contentDescription = stringResource(R.string.settings_ble_cd),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.settings_ble_title),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(if (expanded) R.string.settings_action_collapse else R.string.settings_action_expand)
                )
            }

            // Expanded content
            if (expanded) {
                Divider()
                Column(
                    modifier = Modifier
                        .height(400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    BluetoothLeRoute(
                        paddingValues = PaddingValues(),
                        // navTo = {} // path -> navController.navigate(path) },
                    )
                }
            }
        }
    }
}

// --------------------------------------------
// ABOUT EXPANDABLE
// --------------------------------------------
@Composable
fun AboutExpandable(
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Header row
            Row(
                modifier = Modifier
                    .clickable { onExpandToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.settings_about_cd),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.settings_about_title),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(if (expanded) R.string.settings_action_collapse else R.string.settings_action_expand)
                )
            }

            // Expanded content
            if (expanded) {
                HorizontalDivider()
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.settings_app_version_label, stringResource(R.string.settings_app_version_value_text)))
                    Text(stringResource(R.string.settings_build_number_label, stringResource(R.string.settings_build_number_value_text)))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.settings_about_info_visit_text))
                    Text(
                        text = stringResource(R.string.settings_about_website_url),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutExpandablePreview() {
    AshBikeTheme {
        AboutExpandable(expanded = true, onExpandToggle = { })
    }
}
