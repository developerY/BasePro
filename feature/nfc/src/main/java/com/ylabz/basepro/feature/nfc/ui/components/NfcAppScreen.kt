package com.ylabz.basepro.feature.nfc.ui.components

import android.nfc.NfcEvent
import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.feature.nfc.ui.NfcReadEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.NfcViewModel
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcDisabledScreen
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcNotSupportedScreen
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcWaitingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcAppScreen(
    modifier: Modifier = Modifier,
    uiState: NfcUiState,
    navTo: (String) -> Unit,
    onEvent: (NfcReadEvent) -> Unit
) {
    var selectedTab by remember { mutableStateOf("scan") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("NFC Scanner App") }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == "scan",
                    onClick = { selectedTab = "scan" },
                    icon = { Icon(Icons.Default.Nfc, contentDescription = "Scan") },
                    label = { Text("Scan") }
                )
                NavigationBarItem(
                    selected = selectedTab == "history",
                    onClick = { selectedTab = "history" },
                    icon = { Icon(Icons.Default.List, contentDescription = "History") },
                    label = { Text("History") }
                )
                NavigationBarItem(
                    selected = selectedTab == "settings",
                    onClick = { selectedTab = "settings" },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Status bar to show current NFC status (you can customize this further)
                NfcStatusBar(uiState = uiState)
                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    "scan" -> {
                        when (uiState) {
                            is NfcUiState.NfcNotSupported -> {
                                NfcNotSupportedScreen(
                                    onRetry = { onEvent(NfcReadEvent.Retry) }
                                )
                            }
                            is NfcUiState.NfcDisabled -> {
                                NfcDisabledScreen(
                                    onEnableNfc = { onEvent(NfcReadEvent.EnableNfc) }
                                )
                            }
                            is NfcUiState.Stopped -> {
                                // NFC available but not scanning yet.
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("NFC is ready. Tap the button to start scanning.")
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = { onEvent(NfcReadEvent.StartScan) }) {
                                        Text("Start Scan")
                                    }
                                }
                            }
                            is NfcUiState.WaitingForTag -> {
                                NfcScanScreen(
                                    isScanning = true,
                                    onTagScanned = { tag ->
                                        // Optionally forward the scanned tag to the ViewModel
                                    },
                                    onError = { errorMsg ->
                                        // Optionally handle errors
                                    },
                                    onStopScan = { onEvent(NfcReadEvent.StopScan) }
                                )
                            }

                            is NfcUiState.TagScanned -> {
                                // Display scanned tag data and offer options to stop or restart scanning.
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("NFC Tag Scanned Successfully!")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Tag Info: ${uiState.tagInfo}")
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row {
                                        Button(onClick = { onEvent(NfcReadEvent.StopScan) }) {
                                            Text("Stop Scan")
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Button(onClick = { onEvent(NfcReadEvent.StartScan) }) {
                                            Text("Scan Again")
                                        }
                                    }
                                }
                            }
                            is NfcUiState.Loading -> {
                                LoadingScreen()
                            }
                            is NfcUiState.Error -> {
                                ErrorScreen(
                                    message = uiState.message,
                                    onRetry = { onEvent(NfcReadEvent.Retry) }
                                )
                            }
                        }
                    }
                    "history" -> {
                        NfcHistoryScreen(modifier = Modifier.padding(innerPadding))
                    }
                    "settings" -> {
                        NfcSettingsScreen(modifier = Modifier.padding(innerPadding))
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Unknown Tab")
                        }
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun NfcAppScreenPreview() {
    // Sample data for the preview
    val sampleUiState = NfcUiState.Stopped // or any other state like NfcUiState.TagScanned("Sample Tag Info")
    val sampleNavTo: (String) -> Unit = { route -> println("Navigating to $route") }
    val sampleOnEvent: (com.ylabz.basepro.feature.nfc.ui.NfcReadEvent) -> Unit = { event -> println("Event: $event") }

    // Use a Box to provide a background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray) // Example background color
    ) {
        NfcAppScreen(uiState = sampleUiState, navTo = sampleNavTo, onEvent = sampleOnEvent)
    }
}

// ----- Pre-existing NFC State Screens -----

@Composable
fun NfcNotSupportedScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your device does not support NFC.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun NfcDisabledScreen(onEnableNfc: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("NFC is disabled. Please enable NFC in your device settings.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onEnableNfc) {
            Text("Enable NFC")
        }
    }
}

@Composable
fun NfcWaitingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Waiting for an NFC tag...\nPlease tap your NFC tag now.")
    }
}

@Composable
fun NfcTagScannedScreen(tagInfo: String, onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("NFC Tag Scanned Successfully!")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tag Info: $tagInfo")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onDone) {
            Text("Done")
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Error: $message")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
