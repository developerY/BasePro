package com.ylabz.basepro.feature.nfc.ui.components.screens

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
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcAppScreen(
    modifier: Modifier = Modifier,
    uiState: NfcUiState,
    navTo: (String) -> Unit,
    onEvent: (NfcRwEvent) -> Unit
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
                    selected = selectedTab == "write",
                    onClick = { selectedTab = "write" },
                    icon = { Icon(Icons.Default.Save, contentDescription = "Write") },
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
                // Status bar showing current NFC status.
                NfcStatusBar(uiState = uiState)
                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    "scan" -> {
                        when (uiState) {
                            is NfcUiState.NfcNotSupported -> {
                                NfcNotSupportedScreen(
                                    onRetry = { onEvent(NfcRwEvent.Retry) }
                                )
                            }
                            is NfcUiState.NfcDisabled -> {
                                NfcDisabledScreen(
                                    onEnableNfc = { onEvent(NfcRwEvent.EnableNfc) }
                                )
                            }
                            is NfcUiState.Stopped -> {
                                NfcStoppedScreen(
                                    onEvent = { onEvent(NfcRwEvent.StartScan) }
                                )
                            }
                            is NfcUiState.WaitingForTag -> {
                                NfcScanScreen(
                                    state = uiState as NfcUiState.WaitingForTag,
                                    onEvent = { event -> onEvent(event) }
                                )
                            }
                            is NfcUiState.TagScanned -> {
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
                                    onRetry = { onEvent(NfcRwEvent.Retry) }
                                )
                            }
                            is NfcUiState.Writing,
                            is NfcUiState.WriteSuccess,
                            is NfcUiState.WriteError -> {
                                // When in any write-related state while on the scan tab,
                                // you can decide how to handle it. For now, we'll simply show Loading.
                                LoadingScreen()
                            }
                        }
                    }
                    "write" -> {
                        NfcWriteScreen(
                            state = uiState,
                            onEvent = { event -> onEvent(event) }
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
    val sampleOnEvent: (NfcRwEvent) -> Unit = { event -> println("Event: $event") }

    // Use a Box to provide a background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray) // Example background color
    ) {
        NfcAppScreen(uiState = sampleUiState, navTo = sampleNavTo, onEvent = sampleOnEvent)
    }
}

