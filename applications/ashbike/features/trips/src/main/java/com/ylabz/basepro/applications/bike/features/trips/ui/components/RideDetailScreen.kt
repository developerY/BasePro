package com.ylabz.basepro.applications.bike.features.trips.ui.components


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    modifier: Modifier = Modifier,
    rideId: String,
    onBack: () -> Unit,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is TripsUIState.Loading -> {
                CircularProgressIndicator()
            }

            is TripsUIState.Error -> {
                Text(text = (uiState as TripsUIState.Error).message)
            }

            is TripsUIState.Success -> {
                val ride = (uiState as TripsUIState.Success)
                    .bikeRides
                    .firstOrNull { it.rideId == rideId }

                if (ride == null) {
                    Text("Ride not found", style = MaterialTheme.typography.bodyLarge)
                } else {
                    val path = listOf(
                        LatLng(ride.startLat, ride.startLng),
                        LatLng(ride.endLat, ride.endLng)
                    )
                    val midLat = (ride.startLat + ride.endLat) / 2
                    val midLng = (ride.startLng + ride.endLng) / 2

                    val cameraState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            LatLng(midLat, midLng),
                            13f
                        )
                    }

                    GoogleMap(
                        modifier = Modifier
                            .fillMaxSize(),
                        cameraPositionState = cameraState,
                        uiSettings = MapUiSettings(zoomControlsEnabled = true),
                        properties = MapProperties(mapType = MapType.NORMAL)
                    ) {
                        if (path.size > 1) {
                            Polyline(points = path, width = 5f)
                        }
                        Marker(
                            state = MarkerState(position = path.first()),
                            title = "Start"
                        )
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
