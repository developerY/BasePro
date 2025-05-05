package com.ylabz.basepro.applications.bike.features.main.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.HealthConnectClient
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.BikeDashboardContent
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.unused.ErrorScreen
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.unused.LoadingScreen
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.ConnectionStatus
import com.ylabz.basepro.core.model.bike.NfcData
import com.ylabz.basepro.core.model.health.HealthScreenState
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
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

    // 1) Pull in each VM’s state
    val healthUiState by healthViewModel.uiState.collectAsState()
    val nfcUiState by nfcViewModel.uiState.collectAsState()
    // Collect the UI states from both.
    val bikeUiState by bikeViewModel.uiState.collectAsState()

    // 2) Prepare our health‐connect panel parameters
    val hcAvailable =
        healthViewModel.healthSessionManager.availability.value == HealthConnectClient.SDK_AVAILABLE
    val healthViewModel = healthViewModel
    healthViewModel.permissionsGranted.value

    // Bundle everything into one state object.
    HealthScreenState(
        isHealthConnectAvailable = hcAvailable,
        permissionsGranted = healthViewModel.permissionsGranted.value,
        permissions = healthViewModel.permissions,
        backgroundReadPermissions = healthViewModel.backgroundReadPermissions,
        backgroundReadAvailable = healthViewModel.backgroundReadAvailable.value,
        backgroundReadGranted = healthViewModel.backgroundReadGranted.value
    )

    // 3) One single launcher for *all* health permissions
    rememberLauncherForActivityResult(
        contract = healthViewModel.permissionsLauncher
    ) {
        // once user returns from the system dialog, re‐check & reload
        healthViewModel.onEvent(HealthEvent.RequestPermissions)
    }
    //val healthUiState by remember { mutableStateOf(viewModel.uiState) }
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    val onPermissionsResult = { healthViewModel.initialLoad() }

    // 4) If we’ve never tried to load, kick off the first load
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
    // Sample settings (or use bikeUiState.settings if already loaded)
    // We assume that BikeUiState and HealthUiState must be Success to show the main screen.
    when (bikeUiState) {
        is BikeUiState.Loading -> LoadingScreen()

        is BikeUiState.Error -> {
            ErrorScreen(errorMessage = (bikeUiState as BikeUiState.Error).message) {
                bikeViewModel.onEvent(BikeEvent.LoadBike)
            }
        }
        // Require only bike to be Success. Health is optional/controlled via settings.
        is BikeUiState.Success -> {
            (bikeUiState as BikeUiState.Success).bikeData

            // Health: if available, use the success data; otherwise, use a default and flag it as not enabled.
            when (healthUiState) {
                is HealthUiState.Success -> (healthUiState as HealthUiState.Success).healthData
                else -> null //HealthStats(heartRate = 0, calories = 0.0)
            }
            // Flag whether Health integration is enabled/available.
            healthUiState is HealthUiState.Success

            // NFC: if a tag has been scanned, use its data; otherwise, pass null.
            when (nfcUiState) {
                is NfcUiState.TagScanned -> NfcData((nfcUiState as NfcUiState.TagScanned).tagInfo)
                else -> null
            }

            // Assume the bike state includes a bikeID and battery when connected.
            ConnectionStatus(
                isConnected = false, //bikeState.bikeID != null,
                batteryLevel = 50, //bikeState.battery
            )
            BikeDashboardContent(
                modifier = modifier,
                bikeRideInfo = (bikeUiState as BikeUiState.Success).bikeData,
                onBikeEvent = { bikeViewModel.onEvent(it) },
                navTo = navTo
            )
        }

        else -> {
            LoadingScreen()//modifier)
        }
    }
}

@Preview
@Composable
fun BikeUiRoutePreview() {
    BikeRideInfo(
        // Core location & speeds
        location = LatLng(37.4219999, -122.0862462),
        currentSpeed = 0.0,
        averageSpeed = 0.0,
        maxSpeed = 0.0,

        // Distances (km)
        currentTripDistance = 0.0f,
        totalTripDistance = null,
        remainingDistance = null,

        // Elevation (m)
        elevationGain = 0.0,
        elevationLoss = 0.0,

        // Calories
        caloriesBurned = 0,

        // UI state
        rideDuration = "00:00",
        settings = mapOf(
            "Theme" to listOf("Light", "Dark", "System Default"),
            "Language" to listOf("English", "Spanish", "French"),
            "Notifications" to listOf("Enabled", "Disabled")
        ),
        heading = 0f,
        elevation = 0.0,

        // Bike connectivity
        isBikeConnected = false,
        batteryLevel = null,
        motorPower = null,

        // rideState & weatherInfo use their defaults
    )

    val mockNavTo: (String) -> Unit = {}
    BikeUiRoute(
        navTo = mockNavTo,
    )

}
