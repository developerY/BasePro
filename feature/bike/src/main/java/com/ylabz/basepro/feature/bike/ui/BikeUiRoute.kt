package com.ylabz.basepro.feature.bike.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.feature.bike.ui.components.home.BikeHomeScreen
import com.ylabz.basepro.feature.bike.ui.components.home.BikeHomeSlider
import com.ylabz.basepro.settings.ui.components.ErrorScreen
import com.ylabz.basepro.settings.ui.components.LoadingScreen

@Composable
fun BikeUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: BikeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    when (uiState) {
        is BikeUiState.Loading -> {
            LoadingScreen()
        }
        is BikeUiState.Error -> {
            ErrorScreen(errorMessage = uiState.message) {
                viewModel.onEvent(BikeEvent.LoadBike)
            }
        }
        is BikeUiState.Success -> {
            /*BikeCompose(
                modifier = modifier,
                settings = uiState.settings,
                location = uiState.location,   // <-- Pass the location here
                onEvent = { event -> viewModel.onEvent(event) },
                navTo = navTo
            )*/
            /*BikeHomeScreen(
               modifier = modifier,
               settings = uiState.settings,
               location = uiState.location,   // <-- Pass the location here
               onEvent = { event -> viewModel.onEvent(event) },
               navTo = navTo
           )*/
            BikeHomeSlider()
        }
    }
}

