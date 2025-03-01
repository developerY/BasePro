package com.ylabz.basepro.feature.bike.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen() {
    val context = LocalContext.current
    // Create a FusedLocationProviderClient
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Initialize the camera position state with a default location
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f)
    }

    // Request the current location and update the camera position
    LaunchedEffect(Unit) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    // Animate the camera to the current location with zoom level 15f
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
                    )
                }
            }
        } catch (e: SecurityException) {
            // Handle the exception if location permissions are not granted
        }
    }

    // Display the map with the updated camera position
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true
        )
    )
}
