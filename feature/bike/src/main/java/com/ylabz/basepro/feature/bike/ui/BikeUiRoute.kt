package com.ylabz.basepro.feature.bike.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.feature.bike.ui.components.home.dials.BikeAppScreen
import com.ylabz.basepro.settings.ui.components.ErrorScreen
import com.ylabz.basepro.settings.ui.components.LoadingScreen
import androidx.compose.runtime.getValue

@Composable
fun BikeUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit
) {
    // Obtain both viewmodels from Hilt.
    val bikeViewModel: BikeViewModel = hiltViewModel()
    val healthViewModel: HealthViewModel = hiltViewModel()

    // Collect the UI states from both.
    val bikeUiState by bikeViewModel.uiState.collectAsState()
    val healthUiState by healthViewModel.uiState.collectAsState()

    // Sample settings (or use bikeUiState.settings if already loaded)
    val sampleSettings = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
    )

    when {
        bikeUiState is BikeUiState.Loading || healthUiState is HealthUiState.Loading -> {
            LoadingScreen()//modifier)
        }
        bikeUiState is BikeUiState.Error -> {
            ErrorScreen(errorMessage = (bikeUiState as BikeUiState.Error).message) {
                bikeViewModel.onEvent(BikeEvent.LoadBike)
            }
        }
        healthUiState is HealthUiState.Error -> {
            ErrorScreen(errorMessage = (healthUiState as HealthUiState.Error).message) {
                healthViewModel.onEvent(HealthEvent.LoadHealthData)
            }
        }
        bikeUiState is BikeUiState.Success && healthUiState is HealthUiState.Success -> {
            val bikeState = bikeUiState as BikeUiState.Success
            val healthState = healthUiState as HealthUiState.Success

            BikeAppScreen(
                modifier = modifier,
                currentSpeed = bikeState.currentSpeed,
                currentTripDistance = bikeState.currentDistance,
                totalDistance = bikeState.totalDistance,
                tripDuration = bikeState.rideDuration,
                settings = bikeState.settings, // or sampleSettings if you prefer
                onBikeEvent = { event -> bikeViewModel.onEvent(event) },
                onHealthEvent = { event -> healthViewModel.onEvent(event) }, // Use the instance, not the class name
                location = bikeState.location ?: LatLng(0.0, 0.0),
                sessionsList = healthState.healthData, // Assuming HealthUiState.Success contains healthData.
                navTo = navTo
            )
        }
        else -> {
            LoadingScreen()//modifier)
        }
    }
}
