package com.ylabz.basepro.applications.bike.ui.components.home.dials

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.ui.BikeEvent
import com.ylabz.basepro.applications.bike.ui.components.home.BikeDashboardContent
import com.ylabz.basepro.applications.bike.ui.components.path.BikePathScreen
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.ylabz.basepro.applications.bike.ui.BikeUiState
import com.ylabz.basepro.applications.bike.ui.components.settings.SettingsNavHost
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.health.HealthScreenState
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeAppScreen(
    modifier: Modifier = Modifier,
    healthPermState: HealthScreenState,
    healthState: HealthUiState,
    nfcUiState: NfcUiState,
    nfcEvent: (NfcRwEvent) -> Unit,
    sessionsList : List<ExerciseSessionRecord>,  // Assuming your HealthUiState.Success contains healthData.
    onPermissionsLaunch: (Set<String>) -> Unit,
    backgroundReadPermissions: Set<String>,
    bikeRideInfo: BikeRideInfo,
    bikeUiState : BikeUiState,
    onBikeEvent: (BikeEvent) -> Unit,
    onHealthEvent: (HealthEvent) -> Unit,
    navTo: (String) -> Unit
) {
    // Local state to track the selected tab.
    var selectedTab by remember { mutableStateOf("ride") }

    val sampleSettings = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
    )

    // Local navigation lambda that updates local state and calls external navTo.
    val localNavTo: (String) -> Unit = { route ->
        selectedTab = route
    }

    // Set up the initial camera position for the map.
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.4219999, -122.0862462), 14f)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ash Bike") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { localNavTo("startRide") }
            ) {
                Icon(
                    imageVector = Icons.Filled.Navigation,
                    contentDescription = "Start Ride"
                )
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == "ride",
                    onClick = { localNavTo("ride") },
                    icon = { Icon(Icons.AutoMirrored.Filled.DirectionsBike, contentDescription = "Ride") },
                    label = { Text("Ride") }
                )
                NavigationBarItem(
                    selected = selectedTab == "path",
                    onClick = { localNavTo("path") },
                    icon = { Icon(Icons.AutoMirrored.Filled.AltRoute, contentDescription = "Path") },
                    label = { Text("Path") }
                )
                NavigationBarItem(
                    selected = selectedTab == "settings",
                    onClick = { localNavTo("settings") },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        },
        content = { innerPadding ->
            when (selectedTab) {
                "ride" -> {
                    BikeDashboardContent(
                        modifier = Modifier.padding(innerPadding),
                        bikeRideInfo = bikeRideInfo,
                        nfcUiState = nfcUiState,
                        navTo = navTo
                    )
                }
                "path" -> {
                    BikePathScreen(
                        modifier = Modifier.padding(innerPadding),
                        settings = sampleSettings,
                        onEvent = {},
                        location = LatLng(0.0,0.0),
                        navTo = navTo // No-op for preview
                    )
                }
                "settings" -> {
                    /*SettingsScreenEx(
                        navTo = navTo
                    )*/

                    SettingsNavHost(
                        modifier = Modifier.padding(innerPadding),
                        bikeUiState = bikeUiState,
                        nfcUiState = nfcUiState,
                        nfcEvent = nfcEvent,
                        navTo = navTo
                    )

                    /*FancySettingsScreen(
                        onAppPreferencesClick = {},// navTo("app_preferences") },
                        onBikeConfigurationClick = {},// navTo("bike_configuration") },
                        onProfileClick = {},// navTo("profile") },
                        onAboutClick = {},// navTo("about") }
                    )*/
                    /*BikeSettingsScreen(
                        modifier = modifier,
                        bundledState = healthPermState,
                        healthUiState = healthState,
                        nfcUiState = nfcUiState,
                        sessionsList = sessionsList,  // Assuming your HealthUiState.Success contains healthData.
                        permissionsLauncher = onPermissionsLaunch,
                        settings = sampleSettings,
                        onBikeEvent = onBikeEvent,
                        onHealthEvent = onHealthEvent,
                        nfcEvent = {},
                        navTo = navTo // No-op for preview
                    )*/
                }
                "startRide" -> {
                    // Start Ride screen placeholder.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Start Ride Screen")
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Unknown Route")
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun BikeAppScreenPreview() {
    val healthPermState = HealthScreenState(
        isHealthConnectAvailable = true,
        permissionsGranted = true,
        permissions = emptySet(),
        backgroundReadPermissions = emptySet(),
        backgroundReadAvailable = true,
        backgroundReadGranted = true
    )

    val healthState = HealthUiState.Success(emptyList())

    val bikeRideInfo = BikeRideInfo(
        currentSpeed = 55.0,
        currentTripDistance = 5.0,
        totalDistance = 100.0,
        rideDuration = "00:15:00",
        settings = mapOf("Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")),
        location = LatLng(37.4219999, -122.0862462)
    )

    BikeAppScreen(
        healthPermState = healthPermState,
        healthState = healthState,
        nfcUiState = NfcUiState.WaitingForTag,
        nfcEvent = {},
        sessionsList = emptyList(),
        onPermissionsLaunch = {},
        backgroundReadPermissions = emptySet(),
        bikeRideInfo = bikeRideInfo,
        bikeUiState = BikeUiState.Loading,
        onBikeEvent = {},
        onHealthEvent = {},
        navTo = {}
    )
}




