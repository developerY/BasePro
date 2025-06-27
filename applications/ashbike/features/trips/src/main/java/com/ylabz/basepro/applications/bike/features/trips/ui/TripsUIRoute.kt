package com.ylabz.basepro.applications.bike.features.trips.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.applications.bike.features.trips.ui.components.BikeTripsCompose
import com.ylabz.basepro.applications.bike.features.trips.ui.components.ErrorScreen
import com.ylabz.basepro.applications.bike.features.trips.ui.components.LoadingScreen
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthSideEffect
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel

@Composable
fun TripsUIRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    // Ask Hilt for both viewmodels
    tripsViewModel: TripsViewModel = hiltViewModel(),
    healthViewModel: HealthViewModel = hiltViewModel(),
) {
    // 1. Observe your trips state
    val uiState by tripsViewModel.uiState.collectAsState()

    // 2. Observe your Health Connect state
    val healthUiState by healthViewModel.uiState.collectAsState()

    // 2. Observe the set of already-synced IDs from HealthViewModel
    val syncedIds by healthViewModel.syncedIds.collectAsState()

    // 3. Create the permissions launcher
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = healthViewModel.permissionsLauncher,
        onResult = {
            // After the user responds, reload the health data
            // healthViewModel.initialLoad()
            // After the dialog closes, tell the ViewModel to load the data
            healthViewModel.onEvent(HealthEvent.LoadHealthData)
        }
    )

    // 4. Create an effect that listens for the PermissionsRequired state
    //    This will now launch the dialog from the correct screen.
    // Use LaunchedEffect to listen for side effects from the ViewModel
    LaunchedEffect(Unit) {
        // Trigger the initial load for health data when the screen appears
        healthViewModel.onEvent(HealthEvent.LoadHealthData)

        // Listen for the signal to launch the permission dialog
        healthViewModel.sideEffect.collect { effect ->
            when (effect) {
                is HealthSideEffect.LaunchPermissions -> {
                    permissionsLauncher.launch(effect.permissions)
                }
            }
        }
    }


    // 3. Render based on your trips state
    when (uiState) {
        TripsUIState.Loading -> LoadingScreen()

        is TripsUIState.Error -> ErrorScreen(
            errorMessage = (uiState as TripsUIState.Error).message,
            onRetry      = { tripsViewModel.onEvent(TripsEvent.OnRetry) }
        )

        is TripsUIState.Success -> {
            BikeTripsCompose(
                modifier      = modifier,
                bikeRides     = (uiState as TripsUIState.Success).bikeRides,
                syncedIds     = syncedIds,
                bikeEvent       = { tripsViewModel.onEvent(it) },
                healthEvent   = { healthViewModel.onEvent(it) },
                bikeToHealthConnectRecords = { tripsViewModel.buildHealthConnectRecordsForRide(it) },
                healthUiState = healthUiState,
                navTo         = navTo
            )
        }
    }
}


