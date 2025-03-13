package com.ylabz.basepro.feature.gbird.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ylabz.basepro.core.model.bike.BikeScreenState
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthFeatureWithPermissions
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
import com.ylabz.basepro.feature.heatlh.ui.components.ErrorScreen
import com.ylabz.basepro.feature.heatlh.ui.components.LoadingScreen
import java.util.UUID

@Composable
fun GBirdUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    gbirdViewModel: GbirdViewModel = hiltViewModel(),
    healthViewModel: HealthViewModel = hiltViewModel()
) {

    val healthUiState by remember { mutableStateOf(viewModel.uiState) }
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    val onPermissionsResult = { healthViewModel.initialLoad() }
    val permissionsLauncher =
        rememberLauncherForActivityResult(healthViewModel.permissionsLauncher) {
            onPermissionsResult()
        }


    LaunchedEffect(healthUiState) {
        // If the initial data load has not taken place, attempt to load the data.
        if (healthUiState is HealthUiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [ExerciseSessionViewModel.UiState] provides details of whether the last action was a
        // success or resulted in an error. Where an error occurred, for example in reading and
        // writing to Health Connect, the user is notified, and where the error is one that can be
        // recovered from, an attempt to do so is made.
        if (healthUiState is HealthUiState.Error && errorId.value != (healthUiState as HealthUiState.Error).uuid) {
            //onError(healthUiState.exception)
            //errorId.value = healthUiState.uuid
        }
    }
    // Obtain both viewmodels from Hilt.


    // Collect the UI states from both.
    val bikeUiState by gbirdViewModel.uiState.collectAsState()

    // Sample settings (or use bikeUiState.settings if already loaded)
    val sampleSettings = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
    )



    when {
        bikeUiState is GbirdUiState.Loading || healthUiState is HealthUiState.Loading -> {
            LoadingScreen()//modifier)
        }
        bikeUiState is GbirdUiState.Error -> {
            ErrorScreen(errorMessage = (bikeUiState as GbirdUiState.Error).message) {
                gbirdViewModel.onEvent(GbirdEvent.LoadGbird)
            }
        }
        healthUiState is HealthUiState.Error -> {
            ErrorScreen(errorMessage = (healthUiState as HealthUiState.Error).message) {
                healthViewModel.onEvent(HealthEvent.LoadHealthData)
            }
        }

        healthUiState is HealthUiState.PermissionsRequired -> HealthFeatureWithPermissions {
            // Launch the permissions request
            permissionsLauncher.launch(healthViewModel.permissions)
        }

        bikeUiState is GbirdUiState.Success && healthUiState is HealthUiState.Success -> {
            val bikeState = bikeUiState as GbirdUiState.Success
            val healthState = healthUiState as HealthUiState.Success

            val bikeScreenState = BikeScreenState(
                currentSpeed = bikeState.currentSpeed,
                currentTripDistance = bikeState.currentDistance,
                totalDistance = bikeState.totalDistance,
                rideDuration = bikeState.rideDuration,
                settings = bikeState.settings,
                location = bikeState.location.let { LatLng(it?.longitude ?: 0.0, it?.longitude ?: 0.0) },
                heading = bikeState.heading
            )

            BikeAppScreen(
                modifier = modifier,
                healthPermState = bundledState,
                healthState = healthState,
                sessionsList = (healthUiState as HealthUiState.Success).healthData,
                onPermissionsLaunch = { values ->
                    permissionsLauncher.launch(values)
                },
                backgroundReadPermissions = backgroundReadPermissions,
                bikeScreenState = bikeScreenState,
                onBikeEvent = { event -> gbirdViewModel.onEvent(event) },
                onHealthEvent = { event -> healthViewModel.onEvent(event) }, // Use the instance, not the class name
                navTo = {} // No-op for preview // Lost a day of coding
            )
        }
        else -> {
            LoadingScreen()//modifier)
        }
    }
}
