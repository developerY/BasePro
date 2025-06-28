package com.ylabz.basepro.applications.bike.features.settings.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import com.ylabz.basepro.applications.bike.database.ProfileData
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.settings.ui.components.ProfileInfoCardEx
import com.ylabz.basepro.applications.bike.features.settings.ui.components.health.HealthExpandableEx
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.NfcUiState.Stopped
import com.ylabz.basepro.feature.nfc.ui.components.NfcScanScreen
import com.ylabz.basepro.feature.qrscanner.ui.QRCodeScannerScreen

// —————————————————————————————————————————————————————————
//  PASTEL COLORS
// —————————————————————————————————————————————————————————
private val PastelLavender = Color(0xFFF3E5F5)
private val PastelBlue     = Color(0xFFDCEEFB)
private val PastelLilac    = Color(0xFFEFECF6)
private val PastelGreen    = Color(0xFFDBF1DB)   // for Bike Settings

// —————————————————————————————————————————————————————————
//  SectionHeader WITH “Collapse All” ACTION
// —————————————————————————————————————————————————————————
// --- Key for managing which card is expanded ---
private enum class SettingsCard {
    THEME, APP, ABOUT, HEALTH, NFC, QR, BLE, BIKE
}

// —————————————————————————————————————————————————————————
//  SETTINGS SCREEN (CLEANED UP)
// —————————————————————————————————————————————————————————
// --- Enums for type-safe state management ---
private enum class SectionKey { App, Connectivity, Bike }
private enum class CardKey { Theme, About, Health, Nfc, Qr, Ble, BikeConfig }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsScreenEx(
    modifier: Modifier = Modifier,
    nfcUiState: NfcUiState,
    nfcEvent: (NfcRwEvent) -> Unit,
    uiState: SettingsUiState.Success,
    onEvent: (SettingsEvent) -> Unit,
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
            .background(Color(0xFFF3E5F5)) // PastelLavender
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        // --- Profile Card (Always visible) ---
        item {
            ProfileInfoCardEx(
                profile = uiState.profile,
                isEditing = isEditing,
                onToggleEdit = { isEditing = !isEditing },
                onEvent = onEvent
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- App Settings Section ---
        stickyHeader {
            SectionHeader(
                title = "App Settings",
                expanded = expandedSections.contains(SectionKey.App),
                bgColor = Color(0xFFDCEEFB), // PastelBlue
                onToggle = { toggle(expandedSections, SectionKey.App) }
            )
        }
        if (expandedSections.contains(SectionKey.App)) {
            item {
                ThemeExpandable(
                    expanded = expandedCards.contains(CardKey.Theme),
                    onExpandToggle = { toggle(expandedCards, CardKey.Theme) },
                    currentTheme = uiState.selections["Theme"] ?: "System",
                    onThemeSelected = { }// theme -> onEvent(SettingsEvent.SetTheme(theme)) }
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
                title = "Connectivity",
                expanded = expandedSections.contains(SectionKey.Connectivity),
                bgColor = Color(0xFFEFECF6), // PastelLilac
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
                title = "Bike Settings",
                expanded = expandedSections.contains(SectionKey.Bike),
                bgColor = Color(0xFFDBF1DB), // PastelGreen
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
    onToggle: () -> Unit,
    bgColor: Color
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
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
                contentDescription = if (expanded) "Collapse" else "Expand",
            )
        }
    }
}


// —————————————————————————————————————————————————————————
//  NEW THEME EXPANDABLE CARD
// —————————————————————————————————————————————————————————
@Composable
fun ThemeExpandable(
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    currentTheme: String,
    onThemeSelected: (String) -> Unit
) {
    val themeOptions = listOf("System", "Light", "Dark")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable { onExpandToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.BrightnessMedium,
                    contentDescription = "Theme",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Theme", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = currentTheme, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            if (expanded) {
                HorizontalDivider()
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    themeOptions.forEach { theme ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (theme == currentTheme),
                                    onClick = { onThemeSelected(theme) }
                                )
                                .padding(vertical = 4.dp),
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


@Preview(showBackground = true)
@Composable
fun SettingsScreenExPreview() {
    val dummyProfile = ProfileData(name = "John Doe", heightCm = "180", weightKg = "80")
    val dummyUiState = SettingsUiState.Success(
        options = mapOf(
            "Theme" to listOf("Light", "Dark", "System Default"),
            "Language" to listOf("English", "Spanish", "French"),
            "Notifications" to listOf("Enabled", "Disabled")
        ),
        selections = mapOf(
            "Theme" to "System Default",
            "Language" to "English",
            "Notifications" to "Enabled"
        ),
        profile = dummyProfile
    )
    SettingsScreenEx(
        nfcUiState = Stopped, nfcEvent = {},
        // healthUiState = HealthUiState.Uninitialized, healthEvent = {},
        uiState = dummyUiState, onEvent = {}, navTo = {}
    )
}

// ---------------------------------------------
// NFC Expandable
// ---------------------------------------------
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
            .fillMaxWidth()
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
                    contentDescription = "NFC",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "NFC",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
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

@Preview(showBackground = true)
@Composable
fun NfcExpandableExPreview() {
    // Assuming Stopped is a valid initial state for NfcUiState
    NfcExpandableEx(nfcUiState = Stopped, nfcEvent = {}, expanded = true, onExpandToggle = {}, navTo = {})
}


@Preview(showBackground = true)
@Composable
fun HealthExpandableExPreview() {
    HealthExpandableEx(expanded = true, onExpandToggle = {}, navTo = {})
}

// ---------------------------------------------
// QR Scanner Expandable
// ---------------------------------------------
@Composable
fun QrExpandableEx(
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
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
                    contentDescription = "QR Scanner",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "QR Scanner",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            // Expanded content
            if (expanded) {
                Divider()
                Column(modifier = Modifier.padding(16.dp)) {
                    // TODO: Insert your real QR code UI
                    // Example:
                    Text(text = "QR Code Scanner: (Placeholder)")

                    Spacer(modifier = Modifier.height(8.dp))

                    // For example:
                    QRCodeScannerScreen()
                    Text(text = "QRCodeScannerScreen() goes here.")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QrExpandableExPreview() {
    QrExpandableEx(expanded = true, onExpandToggle = {})
}

// ---------------------------------------------
// PROFILE / BIKE INFO CARD
// ---------------------------------------------
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                    )
                )
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Profile",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Battery: $bikeBattery | Last BikeRide: $lastRide",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileBikeInfoCardExPreview() {
    ProfileBikeInfoCardEx(userName = "John Doe", bikeBattery = "85%", lastRide = "Yesterday", onProfileClick = {})
}

// ---------------------------------------------
// BIKE CONFIGURATION EXPANDABLE
// ---------------------------------------------
@Composable
fun BikeConfigurationEx(
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    navTo: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
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
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Bike Configuration",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            // Expanded content
            if (expanded) {
                Divider()
                Column(modifier = Modifier.padding(16.dp)) {
                    // Example: Motor assistance toggle
                    var motorAssistance by remember { mutableStateOf(true) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Motor Assistance")
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = motorAssistance,
                            enabled = false,
                            onCheckedChange = { motorAssistance = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Example: Gearing slider
                    var gearingLevel by remember { mutableStateOf(5f) }
                    Text("Gearing Level: ${gearingLevel.toInt()}")
                    Slider(
                        enabled = false,
                        value = gearingLevel,
                        onValueChange = { gearingLevel = it },
                        valueRange = 1f..10f
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to advanced screen
                    Button(
                        enabled = false,
                        onClick = { navTo("settings_bike_advanced") }
                    ) {
                        Text("Advanced Bike Settings")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BikeConfigurationExPreview() {
    BikeConfigurationEx(expanded = true, onExpandToggle = {}, navTo = {})
}

// ---------------------------------------------
// APP PREFERENCES EXPANDABLE
// ---------------------------------------------
@Composable
fun AppPreferencesExpandable(
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
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
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "App Preferences",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            // Expanded content
            if (expanded) {
                Divider()
                Column(modifier = Modifier.padding(16.dp)) {
                    // Example: Dark mode toggle
                    var darkModeEnabled by remember { mutableStateOf(false) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Dark Mode")
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            enabled = false,
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Example: Notifications toggle
                    var notificationsEnabled by remember { mutableStateOf(false) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Notifications")
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            enabled = false,
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Example: Unit preference (imperial/metric)
                    var useMetric by remember { mutableStateOf(true) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Use Metric Units")
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            enabled = false,
                            checked = useMetric,
                            onCheckedChange = { useMetric = it }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreferencesExpandablePreview() {
    AppPreferencesExpandable(expanded = true, onExpandToggle = {})
}

@Composable
fun BLEExpandableCard(
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
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
                    contentDescription = "Bluetooth",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Bluetooth (BLE)",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            // Expanded content
            if (expanded) {
                Divider()
                Column(modifier = Modifier.padding(16.dp)) {
                    // TODO: Insert your real BLE UI or scanning logic here.
                    // This is just a placeholder:
                    Text("BLE status: (Placeholder)")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("BLE scanning / device list / connect buttons go here.")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BLEExpandableCardPreview() {
    BLEExpandableCard(expanded = true, onExpandToggle = {})
}


// ---------------------------------------------
// ABOUT EXPANDABLE
// ---------------------------------------------
@Composable
fun AboutExpandable(
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
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
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "About",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            // Expanded content
            if (expanded) {
                HorizontalDivider()
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("App Version: Alpha 0.9.7")
                    Text("Build Number: 43")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("For licenses, support, or more info, visit:")
                    Text(
                        text = "https://www.ashbike.com",
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
    AboutExpandable(expanded = true, onExpandToggle = {})
}

