package com.ylabz.basepro.applications.bike.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.ui.components.home.main.BikeAppScreen
import com.ylabz.basepro.settings.ui.components.ErrorScreen
import com.ylabz.basepro.settings.ui.components.LoadingScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.health.connect.client.HealthConnectClient
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.health.HealthScreenState
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthFeatureWithPermissions
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
import com.ylabz.basepro.feature.nfc.ui.NfcViewModel
import java.util.UUID

@Composable
fun BikeUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    bikeViewModel: BikeViewModel = hiltViewModel(),
    healthViewModel: HealthViewModel = hiltViewModel(),
    nfcViewModel: NfcViewModel = hiltViewModel()
) {
    val nfcUiState by nfcViewModel.uiState.collectAsState()
    val healthUiState by healthViewModel.uiState.collectAsState()
    // Gather state from the ViewModel.
    val isHealthConnectAvailable = healthViewModel.healthSessionManager.availability.value == HealthConnectClient.SDK_AVAILABLE
    val permissionsGranted by healthViewModel.permissionsGranted
    val permissions = healthViewModel.permissions
    val backgroundReadPermissions = healthViewModel.backgroundReadPermissions
    val backgroundReadAvailable by healthViewModel.backgroundReadAvailable
    val backgroundReadGranted by healthViewModel.backgroundReadGranted

    // Bundle everything into one state object.
    val bundledState = HealthScreenState(
        isHealthConnectAvailable = isHealthConnectAvailable,
        permissionsGranted = permissionsGranted,
        permissions = permissions,
        backgroundReadPermissions = backgroundReadPermissions,
        backgroundReadAvailable = backgroundReadAvailable,
        backgroundReadGranted = backgroundReadGranted,
    )

    //val healthUiState by remember { mutableStateOf(viewModel.uiState) }
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
    val bikeUiState by bikeViewModel.uiState.collectAsState()

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

        healthUiState is HealthUiState.PermissionsRequired -> HealthFeatureWithPermissions {
            // Launch the permissions request
            permissionsLauncher.launch(healthViewModel.permissions)
        }

        bikeUiState is BikeUiState.Success && healthUiState is HealthUiState.Success -> {
            val bikeState = bikeUiState as BikeUiState.Success
            val healthState = healthUiState as HealthUiState.Success

            val bikeRideInfo = BikeRideInfo(
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
                nfcUiState = nfcUiState,
                nfcEvent = { event -> nfcViewModel.onEvent(event) },
                sessionsList = (healthUiState as HealthUiState.Success).healthData,
                onPermissionsLaunch = { values ->
                    permissionsLauncher.launch(values)
                },
                backgroundReadPermissions = backgroundReadPermissions,
                bikeRideInfo = bikeRideInfo,
                bikeUiState = bikeUiState,
                onBikeEvent = { event -> bikeViewModel.onEvent(event) },
                onHealthEvent = { event -> healthViewModel.onEvent(event) }, // Use the instance, not the class name
                navTo = navTo // No-op for preview // Lost a day of coding
            )
        }
        else -> {
            LoadingScreen()//modifier)
        }
    }
}
