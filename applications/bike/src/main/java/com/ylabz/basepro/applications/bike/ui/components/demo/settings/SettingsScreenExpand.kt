package com.ylabz.basepro.applications.bike.ui.components.demo.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.ui.BikeUiState
import com.ylabz.basepro.applications.bike.ui.components.demo.settings.SettingsScreenEx
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.components.NfcScanScreen


// ---------------------------------------------
// MAIN SETTINGS SCREEN
// ---------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenEx(
    modifier: Modifier = Modifier,
    bikeUiState: BikeUiState,
    nfcUiState : NfcUiState,
    nfcEvent : (NfcRwEvent) -> Unit,
    navToSettings: (String) -> Unit,
    navTo: (String) -> Unit
) {
    // Track each expandable’s state
    var bikeConfigExpanded by remember { mutableStateOf(false) }
    var appPreferencesExpanded by remember { mutableStateOf(false) }
    var aboutExpanded by remember { mutableStateOf(false) }

    // NEW: State for NFC, Health, and QR expandables
    var nfcExpanded by remember { mutableStateOf(false) }
    var healthExpanded by remember { mutableStateOf(false) }
    var qrExpanded by remember { mutableStateOf(false) }
    var bleExpanded by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 1) Profile / Bike info card at the top
            item {
                ProfileBikeInfoCardEx(
                    userName = "John Doe",
                    bikeBattery = "80%",
                    lastRide = "12.5 km",
                    onProfileClick = {}
                )
            }

            // 2) Section title
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                )
            }

            // 3) Bike Configuration Expandable
            item {
                BikeConfigurationEx(
                    expanded = bikeConfigExpanded,
                    onExpandToggle = { bikeConfigExpanded = !bikeConfigExpanded },
                    navTo = navTo
                )
            }

            // 4) App Preferences Expandable
            item {
                AppPreferencesExpandable(
                    expanded = appPreferencesExpanded,
                    onExpandToggle = { appPreferencesExpanded = !appPreferencesExpanded }
                )
            }

            // 5) About Expandable
            item {
                AboutExpandable(
                    expanded = aboutExpanded,
                    onExpandToggle = { aboutExpanded = !aboutExpanded }
                )
            }

            // -----------------------------
            // NEW Expandable Sections Below
            // -----------------------------

            // 6) NFC Expandable
            item {
                NfcExpandableEx(
                    nfcUiState = nfcUiState,
                    nfcEvent = nfcEvent,
                    expanded = nfcExpanded,
                    onExpandToggle = { nfcExpanded = !nfcExpanded },
                    navTo = navTo
                )
            }

            // 7) Health Expandable
            item {
                HealthExpandableEx(
                    expanded = healthExpanded,
                    onExpandToggle = { healthExpanded = !healthExpanded }
                )
            }

            // 8) QR Scanner Expandable
            item {
                QrExpandableEx(
                    expanded = qrExpanded,
                    onExpandToggle = { qrExpanded = !qrExpanded }
                )
            }

            item {
                BLEExpandableCard(
                    expanded = bleExpanded,
                    onExpandToggle = { bleExpanded = !bleExpanded }
                )
            }
        }
    }
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

// ---------------------------------------------
// Health Expandable
// ---------------------------------------------
@Composable
fun HealthExpandableEx(
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
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Health",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Health Connect",
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
                    // TODO: Insert your real Health UI or pass in your healthUiState, onHealthEvent, etc.
                    // Example:
                    Text(text = "Health Connect Status: (Placeholder)")

                    Spacer(modifier = Modifier.height(8.dp))

                    // For example, show your HealthStartScreen here:
                    // HealthStartScreen(
                    //     healthPermState = ...,
                    //     sessionsList = ...,
                    //     onPermissionsLaunch = ...,
                    //     onEvent = ...,
                    //     navTo = ...
                    // )
                    Text(text = "HealthStartScreen() goes here.")
                }
            }
        }
    }
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
                    // QRCodeScannerScreen()
                    Text(text = "QRCodeScannerScreen() goes here.")
                }
            }
        }
    }
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
                        text = "Battery: $bikeBattery | Last Ride: $lastRide",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
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
                            onCheckedChange = { motorAssistance = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Example: Gearing slider
                    var gearingLevel by remember { mutableStateOf(5f) }
                    Text("Gearing Level: ${gearingLevel.toInt()}")
                    Slider(
                        value = gearingLevel,
                        onValueChange = { gearingLevel = it },
                        valueRange = 1f..10f
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to advanced screen
                    Button(onClick = {navTo("settings_bike_advanced")}) {
                        Text("Advanced Bike Settings")
                    }
                }
            }
        }
    }
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
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Example: Notifications toggle
                    var notificationsEnabled by remember { mutableStateOf(true) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Notifications")
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
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
                            checked = useMetric,
                            onCheckedChange = { useMetric = it }
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
                    Text("App Version: 1.0.3")
                    Text("Build Number: 42")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("For licenses, support, or more info, visit:")
                    Text(
                        text = "https://www.example.com",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Preview SettingsScreen
@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreenEx(
        bikeUiState = BikeUiState.Loading,
        nfcUiState = NfcUiState.NfcNotSupported,
        nfcEvent = {},
        navToSettings = {},
        navTo = {}
    )
}


