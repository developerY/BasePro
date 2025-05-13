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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BikeScooter
import androidx.compose.material.icons.filled.Bluetooth
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
@Composable
fun SectionHeader(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    bgColor: Color
) {
    Surface(
        tonalElevation  = 4.dp,
        shadowElevation = 4.dp,
        shape           = RoundedCornerShape(8.dp),
        color           = bgColor,
        modifier        = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 6.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text  = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Icon(
                imageVector        = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint               = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// —————————————————————————————————————————————————————————
//  SETTINGS SCREEN WITH “Collapse all” BUTTONS
// —————————————————————————————————————————————————————————
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

    // per-section open/closed
    var appExpanded by rememberSaveable { mutableStateOf(false) }
    var connExpanded by rememberSaveable { mutableStateOf(false) }
    var bikeSettingsExpanded by rememberSaveable { mutableStateOf(false) }



    // All expandable states
    val expandables = remember {
        mutableStateMapOf(
            "bike"   to false,
            "app"    to false,
            "about"  to false,
            "nfc"    to false,
            "health" to false,
            "qr"     to false,
            "ble"    to false
        )
    }
    var isEditing by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PastelLavender)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Profile
            item {
                ProfileInfoCardEx(
                    profile = uiState.profile,
                    isEditing = isEditing,
                    onToggleEdit = { isEditing = !isEditing },
                    onEvent = onEvent
                )
            }

            // ——————————————————————————————
            // App Settings Section
            // ——————————————————————————————

            // — App Settings Section —
            stickyHeader {
                SectionHeader(
                    title = "App Settings",
                    expanded = appExpanded,
                    bgColor = PastelBlue,
                    onToggle = {
                        if (appExpanded) {
                            // collapsing: also collapse children
                            listOf("bike", "app", "about").forEach { expandables[it] = false }
                        }
                        appExpanded = !appExpanded
                    }
                )
            }
            if (appExpanded) {
                item {
                    AppPreferencesExpandable(
                        expanded = expandables["app"] == true,
                        onExpandToggle = {
                            expandables["app"] = !(expandables["app"] ?: false)
                        }
                    )
                }
                item {
                    AboutExpandable(
                        expanded = expandables["about"] == true,
                        onExpandToggle = {
                            expandables["about"] = !(expandables["about"] ?: false)
                        }
                    )
                }
            }
            // ——————————————————————————————
            // Connectivity Section
            // ——————————————————————————————
            stickyHeader {
                SectionHeader(
                    title = "Connectivity",
                    expanded = connExpanded,
                    bgColor = PastelLilac,
                    onToggle = {
                        if (connExpanded) {
                            listOf("health", "nfc", "qr", "ble").forEach { expandables[it] = false }
                        }
                        connExpanded = !connExpanded
                    }
                )
            }
            if (connExpanded) {
                item {
                    HealthExpandableEx(
                        expanded = expandables["health"] == true,
                        onExpandToggle = {
                            expandables["health"] = !(expandables["health"] ?: false)
                        },
                        navTo = navTo
                    )
                }
                // add your visual break here
                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
                item {
                    NfcExpandableEx(
                        nfcUiState = nfcUiState,
                        nfcEvent = nfcEvent,
                        expanded = expandables["nfc"] == true,
                        onExpandToggle = {
                            expandables["nfc"] = !(expandables["nfc"] ?: false)
                        },
                        navTo = navTo
                    )
                }
                item {
                    QrExpandableEx(
                        expanded = expandables["qr"] == true,
                        onExpandToggle = {
                            expandables["qr"] = !(expandables["qr"] ?: false)
                        }
                    )
                }
                item {
                    BLEExpandableCard(
                        expanded = expandables["ble"] == true,
                        onExpandToggle = {
                            expandables["ble"] = !(expandables["ble"] ?: false)
                        }
                    )
                }
            }


            // — Bike Settings Section ————————————————————————————————
            stickyHeader {
                SectionHeader(
                    title    = "Bike Settings",
                    expanded = bikeSettingsExpanded,
                    bgColor  = PastelGreen,
                    onToggle = {
                        if (bikeSettingsExpanded) {
                            expandables["bike"] = false
                        }
                        bikeSettingsExpanded = !bikeSettingsExpanded
                    }
                )
            }
            if (bikeSettingsExpanded) {
                item {
                    BikeConfigurationEx(
                        expanded       = expandables["bike"] == true,
                        onExpandToggle = { expandables["bike"] = !expandables["bike"]!! },
                        navTo          = navTo
                    )
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

