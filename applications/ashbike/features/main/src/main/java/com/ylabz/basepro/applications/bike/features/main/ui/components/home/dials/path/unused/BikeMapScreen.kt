package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path.unused

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
//import androidx.compose.ui.tooling.preview.Preview
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    loc: LatLng? = null  // Optional location from ViewModel
) {
    // Initialize the camera position state with a default location
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f)
    }

    // Update the camera position when 'loc' changes
    LaunchedEffect(loc) {
        if (loc != null) {
            // Animate to the provided location
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(loc, 15f)
            )
        }
    }

    // Display the Google Map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true),
        uiSettings = MapUiSettings(zoomControlsEnabled = true)
    )
}
/*
@Preview
@Composable
fun MapScreenPreview() {
    val loc = LatLng(37.7749, -122.4194)
    MapScreen(loc = loc)
}
*/

