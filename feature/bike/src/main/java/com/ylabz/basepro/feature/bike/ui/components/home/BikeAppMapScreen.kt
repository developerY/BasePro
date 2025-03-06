package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.History
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
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.bike.ui.BikeEvent
import com.ylabz.basepro.feature.bike.ui.components.BikeCompose
import com.ylabz.basepro.feature.bike.ui.components.settings.BikeSettingsOneScreen
import com.ylabz.basepro.settings.ui.components.BikeRouteScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeAppMapScreen(
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
                    selected = selectedTab == "history",
                    onClick = { localNavTo("history") },
                    icon = { Icon(Icons.Filled.History, contentDescription = "History") },
                    label = { Text("History") }
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
                    BikeCompose(
                        modifier = Modifier.padding(innerPadding),
                        settings = sampleSettings,
                        onEvent = {},
                        location = LatLng(0.0,0.0),
                        navTo = {} // No-op for preview
                    )
                }
                "history" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Ride screen shows the full-screen map.
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(isMyLocationEnabled = true),
                            uiSettings = MapUiSettings(zoomControlsEnabled = true)
                        )
                    }
                }
                "settings" -> {
                    BikeRouteScreen(
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
        BikeAppMapScreen(
            settings = sampleSettings,
            location = LatLng(0.0, 0.0),
            onEvent = {},
            navTo = {}
        )
    }
}
