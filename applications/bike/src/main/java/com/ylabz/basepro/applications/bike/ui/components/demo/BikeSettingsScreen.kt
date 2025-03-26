package com.ylabz.basepro.applications.bike.ui.components.demo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.ui.BikeEvent
import com.ylabz.basepro.core.model.health.HealthScreenState
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.components.HealthStartScreen
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.components.NfcScanScreen
import com.ylabz.basepro.feature.qrscanner.ui.QRCodeScannerScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BikeScooter
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.ylabz.basepro.settings.ui.components.BikeSettingsScreen
import com.ylabz.basepro.settings.ui.components.ErrorScreen
import com.ylabz.basepro.settings.ui.components.LoadingScreen

@Composable
fun BikeSettingsScreenEx(
    modifier: Modifier = Modifier,
    bundledState: HealthScreenState,
    healthUiState: HealthUiState,
    nfcUiState: NfcUiState,
    sessionsList: List<ExerciseSessionRecord>,
    permissionsLauncher: (Set<String>) -> Unit,
    settings: Map<String, List<String>>,
    onBikeEvent: (BikeEvent) -> Unit,
    onHealthEvent: (HealthEvent) -> Unit,
    nfcEvent: (NfcRwEvent) -> Unit,
    navTo: (String) -> Unit
) {
    // Track whether each card is expanded
    var nfcExpanded by remember { mutableStateOf(false) }
    var healthExpanded by remember { mutableStateOf(false) }
    var qrExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navTo("home_screen") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Navigate to Home"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1) Radio-button settings
            settings.forEach { (key, options) ->
                var selectedOption by remember { mutableStateOf(options.first()) }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        options.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    selectedOption = option
                                }
                            ) {
                                RadioButton(
                                    selected = (selectedOption == option),
                                    onClick = { selectedOption = option }
                                )
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            onBikeEvent(BikeEvent.UpdateSetting(key, selectedOption))
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save $key")
                    }
                }
            }

            // 2) "Delete All Entries" button
            Button(
                onClick = { onBikeEvent(BikeEvent.DeleteAllEntries) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Delete All Entries")
            }

            // 3) NFC Expandable Card
            ExpandableSection(
                title = "NFC Status: ${nfcUiState.toReadableString()}",
                expanded = nfcExpanded,
                onExpandToggle = { nfcExpanded = !nfcExpanded }
            ) {
                // If expanded, show your NFC UI
                NfcScanScreen(
                    uiState = nfcUiState,
                    onEvent = nfcEvent,
                    navTo = navTo
                )
            }

            // 4) Health Connect Expandable Card
            ExpandableSection(
                title = "Health Connect",
                expanded = healthExpanded,
                onExpandToggle = { healthExpanded = !healthExpanded }
            ) {
                when (healthUiState) {
                    is HealthUiState.Loading -> {
                        LoadingScreen()
                    }
                    is HealthUiState.Error -> {
                        ErrorScreen(
                            errorMessage = healthUiState.message,
                            onRetry = {
                                // For example, re-check permissions or re-load data
                                //onHealthEvent(HealthEvent.CheckPermissions)
                            }
                        )
                    }
                    is HealthUiState.Success -> {
                        HealthStartScreen(
                            modifier = modifier,
                            healthPermState = bundledState,
                            sessionsList = healthUiState.healthData,
                            onPermissionsLaunch = permissionsLauncher,
                            onEvent = onHealthEvent,
                            navTo = navTo
                        )
                    }

                    is HealthUiState.PermissionsRequired -> TODO()
                    HealthUiState.Uninitialized -> TODO()
                }
            }

            // 5) (Optional) QR Code Scanner Expandable
            ExpandableSection(
                title = "QR Scanner",
                expanded = qrExpanded,
                onExpandToggle = { qrExpanded = !qrExpanded }
            ) {
                QRCodeScannerScreen()
            }
        }
    }
}

/**
 * Simple helper function to show a human-readable NFC status.
 * Adjust to your actual NfcUiState values.
 */
private fun NfcUiState.toReadableString(): String = when (this) {
    NfcUiState.NfcNotSupported -> "NFC Not Supported"
    NfcUiState.NfcDisabled -> "NFC Disabled"
   // NfcUiState.NfcEnabled -> "NFC Enabled"
    // etc.
    is NfcUiState.Error -> TODO()
    NfcUiState.Loading -> TODO()
    NfcUiState.Stopped -> TODO()
    is NfcUiState.TagScanned -> TODO()
    NfcUiState.WaitingForTag -> TODO()
    is NfcUiState.WriteError -> TODO()
    is NfcUiState.WriteSuccess -> TODO()
    NfcUiState.Writing -> TODO()
}


@Preview
@Composable
fun BikeSettingsScreenPreview() {
    val settings = mapOf(
        "Setting 1" to listOf("Option A", "Option B", "Option C"),
        "Setting 2" to listOf("Option X", "Option Y")
    )

    val bundledState = HealthScreenState(
        isHealthConnectAvailable = true,
        permissionsGranted = true,
        permissions = emptySet(),
        backgroundReadPermissions = emptySet(),
        backgroundReadAvailable = true,
        backgroundReadGranted = true
    )
    val healthUiState = HealthUiState.Success(listOf())
    val nfcUiState = NfcUiState.NfcNotSupported
    val sessionsList = listOf<ExerciseSessionRecord>()
    BikeSettingsScreenEx(
        modifier = Modifier,
        bundledState = bundledState,
        healthUiState = healthUiState,
        nfcUiState = nfcUiState,
        sessionsList = sessionsList,
        permissionsLauncher = { },
        settings = settings,
        onBikeEvent = { },
        onHealthEvent = { },
        nfcEvent = {},
        navTo = { })
}

