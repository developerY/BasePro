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
    uiState: TripsUIState,
    onEvent: (TripsEvent) -> Unit,
    navTo: (String) -> Unit
) {
    val healthViewModel = hiltViewModel<HealthViewModel>()
    val healthUiState by healthViewModel.uiState.collectAsState()

    when (uiState) {
        TripsUIState.Loading -> {
            LoadingScreen()
        }
        is TripsUIState.Error -> {
            ErrorScreen(
                errorMessage = uiState.message,
                onRetry = { onEvent(TripsEvent.OnRetry) }
            )
        }
        is TripsUIState.Success -> {
            BikeTripsCompose(
                modifier   = modifier,
                bikeRides  = uiState.bikeRides,
                onEvent    = onEvent,
                healthEvent = { event : HealthEvent -> healthViewModel.onEvent(event) },
                healthUiState = healthUiState,
                navTo      = navTo
            )
        }
    }
}
