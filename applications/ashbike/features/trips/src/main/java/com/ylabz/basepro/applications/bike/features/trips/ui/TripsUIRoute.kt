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
    navTo: (String) -> Unit,
    viewModel: TripsViewModel = hiltViewModel()
) {
   val uiState = viewModel.uiState.collectAsState().value
    when (uiState) {
        is TripsUIState.Loading -> {
            LoadingScreen()
        }
        is TripsUIState.Error -> {
            ErrorScreen(errorMessage = uiState.message) {
                viewModel.onEvent(TripsEvent.OnRetry)
            }
        }
        is TripsUIState.Success -> {
            Column(modifier = modifier) {
                BikeTripsCompose(
                    modifier = modifier,
                    bikePro = uiState.bikePro,
                    bikeRides = uiState.bikeRides,
                    onEvent = { event -> viewModel.onEvent(event) },
                    navTo = navTo
                )
            }
        }
    }
}
