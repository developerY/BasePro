package com.ylabz.basepro.applications.bike.features.trips.ui.components


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Straight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsUIState
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideDetailScreen(
    rideId: String,
    onBack: () -> Unit,
    viewModel: TripsViewModel = hiltViewModel()
) {
    // Reuse your existing TripsUIState
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ride Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Straight, contentDescription = "Back") // Icons.AutoMirrored.Filled.ArrowBack
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            is TripsUIState.Loading -> {
                Box(Modifier
                    .fillMaxSize()
                    .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TripsUIState.Error -> {
                Text(
                    text = (uiState as TripsUIState.Error).message,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }
            is TripsUIState.Success -> {
                // Find the selected ride
                val rides = (uiState as TripsUIState.Success).bikeRides
                val ride = rides.firstOrNull { it.rideId == rideId }

                if (ride == null) {
                    Text(
                        "Ride not found",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    // Build a straight-line path from start to end
                    val path = listOf(
                        LatLng(ride.startLat, ride.startLng),
                        LatLng(ride.endLat,   ride.endLng)
                    )

                    // Center the camera midway
                    val midLat = (ride.startLat + ride.endLat) / 2
                    val midLng = (ride.startLng + ride.endLng) / 2
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            LatLng(midLat, midLng),
                            13f
                        )
                    }

                    GoogleMap(
                        modifier             = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        cameraPositionState  = cameraPositionState,
                        uiSettings           = MapUiSettings(zoomControlsEnabled = true),
                        properties           = MapProperties(mapType = MapType.NORMAL)
                    ) {
                        // Draw a line between start & end
                        Polyline(points = path, width = 5f)
                        // Mark start
                        Marker(
                            state = MarkerState(position = path.first()),
                            title = "Start"
                        )
                        // Mark end
                        Marker(
                            state = MarkerState(position = path.last()),
                            title = "End"
                        )
                    }
                }
            }
        }
    }
}
