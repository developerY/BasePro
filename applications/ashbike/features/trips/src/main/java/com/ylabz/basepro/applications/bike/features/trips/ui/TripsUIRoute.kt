package com.ylabz.basepro.applications.bike.features.trips.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.applications.bike.features.trips.ui.components.BikeTripsCompose
import com.ylabz.basepro.applications.bike.features.trips.ui.components.ErrorScreen
import com.ylabz.basepro.applications.bike.features.trips.ui.components.LoadingScreen
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
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
                onEvent       = { tripsViewModel.onEvent(it) },
                healthEvent   = { healthViewModel.onEvent(it) },
                healthUiState = healthUiState,
                navTo         = navTo
            )
        }
    }
}

