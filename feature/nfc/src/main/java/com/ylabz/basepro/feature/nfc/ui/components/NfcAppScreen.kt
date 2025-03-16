package com.ylabz.basepro.feature.nfc.ui.components

import android.nfc.NfcEvent
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.feature.nfc.ui.NfcReadEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.NfcViewModel
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcDisabledScreen
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcNotSupportedScreen

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
            when (selectedTab) {
                "scan" -> {
                    // Render NFC scanning UI based on the current NFC UI state.
                    when (uiState) {
                        is NfcUiState.NfcNotSupported -> {
                            NfcNotSupportedScreen(onRetry = { /*onEvent(NfcEvent.Retry)*/ })
                        }
                        is NfcUiState.NfcDisabled -> {
                            NfcDisabledScreen(onEnableNfc = { /*onEvent(NfcEvent.EnableNfc)*/ })
                        }
                        is NfcUiState.WaitingForTag -> {
                            NfcWaitingScreen()
                        }
                        is NfcUiState.TagScanned -> {
                            NfcTagScannedScreen(
                                tagInfo = uiState.tagInfo,
                                onDone = { navTo("nextScreen") }
                            )
                        }
                        is NfcUiState.Idle -> {
                            // Optionally show the waiting screen if idle.
                            NfcWaitingScreen()
                        }
                        else -> {
                            // Fallback to loading.
                            LoadingScreen()
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
    )
}



// ----- Example Stub Composables for Additional Screens -----

@Composable
fun NfcHistoryScreen(modifier: Modifier = Modifier) {
    // This is a stub. You can fill it with your history list of scanned NFC tags.
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("History Screen: List of scanned tags")
    }
}

@Composable
fun NfcSettingsScreen(modifier: Modifier = Modifier) {
    // This is a stub. Add any NFC-related settings or app preferences here.
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Settings Screen")
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
