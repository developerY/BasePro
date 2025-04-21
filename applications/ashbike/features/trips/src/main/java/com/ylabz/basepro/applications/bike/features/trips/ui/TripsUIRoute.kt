package com.ylabz.basepro.applications.bike.features.trips.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.applications.bike.features.trips.ui.components.BikeTripsCompose
import com.ylabz.basepro.applications.bike.features.trips.ui.components.ErrorScreen
import com.ylabz.basepro.applications.bike.features.trips.ui.components.LoadingScreen

@Composable
fun TripsUIRoute(
    modifier: Modifier = Modifier,
    uiState: TripsUIState,
    onEvent: (TripsEvent) -> Unit,
    navTo: (String) -> Unit
) {
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
                navTo      = navTo
            )
        }
    }
}
