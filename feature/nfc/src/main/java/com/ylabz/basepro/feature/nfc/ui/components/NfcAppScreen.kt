package com.ylabz.basepro.feature.nfc.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.feature.nfc.ui.NfcReadEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcWriteScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.ErrorScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.LoadingScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.NfcDisabledScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.NfcNotSupportedScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.NfcHistoryScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.NfcScanScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.NfcSettingsScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.NfcStatusBar
import com.ylabz.basepro.feature.nfc.ui.components.screens.NfcStoppedScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.TagScanned

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
                    selected = selectedTab == "save",
                    onClick = { selectedTab = "save" },
                    icon = { Icon(Icons.Default.Save, contentDescription = "Save") },
                    label = { Text("Write") }
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
                                NfcStoppedScreen(
                                    onEvent = { onEvent(NfcReadEvent.StartScan) }
                                )
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
                               TagScanned(
                                    uiState = uiState,
                                    onEvent = onEvent
                                )
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
                    "save" -> {
                        NfcWriteScreen(
                            modifier = Modifier.padding(innerPadding),
                            isWriting = uiState is NfcUiState.TagScanned,
                            textToWrite = (uiState as? NfcUiState.TagScanned)?.tagInfo ?: "",
                            onTextChange = TODO(),
                            onStartWrite = TODO(),
                            onStopWrite = TODO(),
                        )
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
    val sampleOnEvent: (NfcReadEvent) -> Unit = { event -> println("Event: $event") }

    // Use a Box to provide a background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray) // Example background color
    ) {
        NfcAppScreen(uiState = sampleUiState, navTo = sampleNavTo, onEvent = sampleOnEvent)
    }
}

