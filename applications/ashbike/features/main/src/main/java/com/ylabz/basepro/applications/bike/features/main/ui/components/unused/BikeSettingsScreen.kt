package com.ylabz.basepro.applications.bike.features.main.ui.components.unused

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.core.model.health.HealthScreenState
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.components.HealthStartScreen
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.components.NfcScanScreen
//import com.ylabz.basepro.feature.qrscanner.ui.QRCodeScannerScreen


@Composable
fun BikeSettingsScreen(
    modifier: Modifier = Modifier,
    bundledState: HealthScreenState,
    healthUiState: HealthUiState,
    nfcUiState: NfcUiState,
    sessionsList : List<ExerciseSessionRecord>,  // Assuming your HealthUiState.Success contains healthData.
    permissionsLauncher: (Set<String>) -> Unit,
    settings: Map<String, List<String>>, // Each setting now has a list of options
    onBikeEvent: (BikeEvent) -> Unit,
    onHealthEvent: (HealthEvent) -> Unit,
    nfcEvent: (NfcRwEvent) -> Unit,
    navTo: (String) -> Unit // Navigation callback for FAB
) {
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
            // Display each setting with compact RadioButton options
            settings.forEach { (key, options) ->
                var selectedOption by remember { mutableStateOf(options.first()) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    // Row for each setting's options
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        options.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { selectedOption = option }
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
                        onClick = { onBikeEvent(BikeEvent.UpdateSetting(key, selectedOption)) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save $key")
                    }
                }
            }

            // Spacer(modifier = Modifier.weight(1f))

            // Button to delete all entries
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

            NfcScanScreen(
                uiState = nfcUiState,
                onEvent = nfcEvent,
                navTo = navTo
            )


            HealthStartScreen(
                modifier = modifier,
                healthPermState = bundledState,
                sessionsList = (healthUiState as HealthUiState.Success).healthData,
                onPermissionsLaunch = permissionsLauncher,
                onEvent = onHealthEvent,
                navTo = navTo,
                )

            //QRCodeScannerScreen()


        }
    }
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
    BikeSettingsScreen(
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


// These will be move to a common directory.
@Composable
fun LoadingScreen() {
    Text(text = "Loading...", modifier = Modifier.fillMaxSize())
}

@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(
            text = "Error: $errorMessage",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Retry",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .clickable { onRetry() }
                .padding(vertical = 8.dp)
        )
    }
}

// Preview
@Preview
@Composable
fun LoadingScreenPreview() {
    LoadingScreen()
}

