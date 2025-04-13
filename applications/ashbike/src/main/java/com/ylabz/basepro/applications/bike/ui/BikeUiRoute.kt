package com.ylabz.basepro.applications.bike.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.applications.bike.ui.components.unused.ErrorScreen
import com.ylabz.basepro.applications.bike.ui.components.unused.LoadingScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.health.connect.client.HealthConnectClient
import com.ylabz.basepro.applications.bike.ui.components.home.BikeDashboardContent
import com.ylabz.basepro.core.model.bike.ConnectionStatus
import com.ylabz.basepro.core.model.bike.NfcData
import com.ylabz.basepro.core.model.health.HealthScreenState
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.NfcViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.core.model.bike.BikeRideInfo
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



    // We assume that BikeUiState and HealthUiState must be Success to show the main screen.
    when {
        bikeUiState is BikeUiState.Loading -> {
            LoadingScreen()
        }
        bikeUiState is BikeUiState.Error -> {
            ErrorScreen(errorMessage = (bikeUiState as BikeUiState.Error).message) {
                bikeViewModel.onEvent(BikeEvent.LoadBike)
            }
        }
        // Require only bike to be Success. Health is optional/controlled via settings.
        bikeUiState is BikeUiState.Success -> {
            val bikeState = bikeUiState as BikeUiState.Success

            // Health: if available, use the success data; otherwise, use a default and flag it as not enabled.
            val healthStats = when (healthUiState) {
                is HealthUiState.Success -> (healthUiState as HealthUiState.Success).healthData
                else -> null //HealthStats(heartRate = 0, calories = 0.0)
            }
            // Flag whether Health integration is enabled/available.
            val healthEnabled = healthUiState is HealthUiState.Success

            // NFC: if a tag has been scanned, use its data; otherwise, pass null.
            val nfcData = when (nfcUiState) {
                is NfcUiState.TagScanned -> NfcData((nfcUiState as NfcUiState.TagScanned).tagInfo)
                else -> null
            }

            // Assume the bike state includes a bikeID and battery when connected.
            val connectionStatus = ConnectionStatus(
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
    val bikeRideInfo = BikeRideInfo(
        isBikeConnected = true,
        location = LatLng(37.4219999, -122.0862462),
        currentSpeed = 55.0,
        currentTripDistance = 5.0f,
        totalTripDistance = 100.0f,
        remainingDistance = 50.0f,
        rideDuration = "00:15:00",
        settings = mapOf("Theme" to listOf("Light", "Dark", "System Default"),
            "Language" to listOf("English", "Spanish", "French"),
            "Notifications" to listOf("Enabled", "Disabled")),
        averageSpeed = 12.0,
        elevation = 12.0,
        heading = 12.0f,
        batteryLevel = 12,
        motorPower = 12.0f
    )

    val mockNavTo: (String) -> Unit = {}
    BikeUiRoute(
        navTo = mockNavTo,
    )

}
