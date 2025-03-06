package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.feature.bike.ui.BikeEvent
import com.ylabz.basepro.feature.bike.ui.components.path.BikePathScreen
import com.ylabz.basepro.settings.ui.components.BikeSettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeAppScreen(
    modifier: Modifier = Modifier,
    settings: Map<String, List<String>>,
    location: LatLng?,
    onEvent: (BikeEvent) -> Unit,
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
        navTo(route)
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
                        currentSpeed = 28.3,
                        totalDistance = 12.5,
                        currentTripDistance = 7.2,  // current progress (km)
                        tripDuration = "00:45:30",
                        averageSpeed = 25.0,
                        elevation = 150.0
                    )
                }
                "path" -> {
                    BikePathScreen(
                        modifier = Modifier.padding(innerPadding),
                        settings = sampleSettings,
                        onEvent = {},
                        location = LatLng(0.0,0.0),
                        navTo = {} // No-op for preview
                    )
                }
                "settings" -> {
                    BikeSettingsScreen(
                        modifier = Modifier.padding(innerPadding),
                        settings = sampleSettings,
                        onEvent = {},
                        navTo = {} // No-op for preview
                    )
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

@Preview(showBackground = true)
@Composable
fun BikeAppMapScreenPreview() {
    val sampleSettings = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
    )
    MaterialTheme {
        BikeAppScreen(
            settings = sampleSettings,
            location = LatLng(0.0, 0.0),
            onEvent = {},
            navTo = {}
        )
    }
}
