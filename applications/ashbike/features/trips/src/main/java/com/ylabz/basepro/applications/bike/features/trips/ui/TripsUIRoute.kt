package com.ylabz.basepro.applications.bike.features.trips.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel

@Composable
fun TripsUIRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    // Ask Hilt for both viewmodels
    tripsViewModel: TripsViewModel = hiltViewModel(),
    healthViewModel: HealthViewModel = hiltViewModel(),
) {
    // 1. Observe your trips state (which now contains BikeRideUiModel)
    val uiState by tripsViewModel.uiState.collectAsState()

    // 2. Create the permissions launcher (this remains the same)
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = healthViewModel.permissionsLauncher,
        onResult = {
            healthViewModel.onEvent(HealthEvent.LoadHealthData)
        }
    )

    // Effect handler for HealthViewModel (remains the same)
    LaunchedEffect(Unit) {
        healthViewModel.sideEffect.collect { effect ->
            when (effect) {
                is HealthSideEffect.LaunchPermissions -> {
                    permissionsLauncher.launch(effect.permissions)
                }
                is HealthSideEffect.BikeRideSyncedToHealth -> {
                    tripsViewModel.markRideAsSyncedInLocalDb(
                        rideId = effect.rideId,
                        healthConnectId = effect.healthConnectId
                    )
                }
            }
        }
    }

    // NEW: Effect handler for TripsViewModel
    LaunchedEffect(Unit) {
        tripsViewModel.sideEffect.collect { effect ->
            when (effect) {
                is TripsSideEffect.RequestHealthConnectSync -> {
                    Log.d("TripsUIRoute", "Sync requested for rideId: ${effect.rideId}. Sending to HealthViewModel.")
                    healthViewModel.onEvent(
                        HealthEvent.Insert(
                            rideId = effect.rideId,
                            records = effect.records
                        )
                    )
                }
            }
        }
    }

    // Render based on your trips state
    when (val state = uiState) {
        TripsUIState.Loading -> LoadingScreen()

        is TripsUIState.Error -> ErrorScreen(
            errorMessage = state.message,
            onRetry      = { tripsViewModel.onEvent(TripsEvent.OnRetry) }
        )

        is TripsUIState.Success -> {
            // Note the simplified parameters passed to BikeTripsCompose
            BikeTripsCompose(
                modifier      = modifier,
                bikeRides     = state.bikeRides, // This is now List<BikeRideUiModel>
                onDeleteClick = { rideId ->
                    tripsViewModel.onEvent(TripsEvent.DeleteItem(rideId))
                },
                onSyncClick = { rideId ->
                    // This triggers the new side effect flow in TripsViewModel
                    tripsViewModel.onEvent(TripsEvent.SyncRide(rideId))
                },
                navTo         = navTo
            )
        }
    }
}
