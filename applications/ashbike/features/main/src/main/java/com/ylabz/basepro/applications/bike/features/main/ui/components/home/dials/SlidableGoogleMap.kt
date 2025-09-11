package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState

@Composable
fun SlidableGoogleMap(
    modifier: Modifier = Modifier,
    uiState: BikeUiState.Success,
    onClose: () -> Unit,
    showMapContent: Boolean = true // New parameter to control map display
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp), 
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.large 
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (showMapContent) {
                val currentLocation = remember(uiState.bikeData.location) {
                    uiState.bikeData.location ?: LatLng(0.0, 0.0)
                }
                var isMapReady by remember { mutableStateOf(false) }
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
                }
                val markerState = rememberMarkerState(position = currentLocation)

                LaunchedEffect(currentLocation, isMapReady) {
                    if (isMapReady) {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(currentLocation, 15f),
                            durationMs = 700
                        )
                        markerState.position = currentLocation
                    }
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapLoaded = { isMapReady = true }
                ) {
                    Marker(
                        state = markerState, 
                        title = "Current Location"
                    )
                }
            } else {
                // Fallback UI: Green Screen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Green.copy(alpha = 0.3f)) // Semi-transparent green
                ) {
                    // Optionally, you can add a text message here too
                    // Text("Map is unavailable", Modifier.align(Alignment.Center))
                }
            }

            // Close button is always visible on the card
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), shape = MaterialTheme.shapes.small)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close Map",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
