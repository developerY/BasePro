package com.ylabz.basepro.applications.bike.features.trips.ui.components


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.ylabz.basepro.applications.bike.database.BikeRideEntity




import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.ylabz.basepro.applications.bike.database.RideWithLocations
import com.ylabz.basepro.applications.bike.database.mapper.BikeRide

@Composable
fun RideDetailScreen(
    modifier: Modifier = Modifier,
    rideWithLocs: RideWithLocations?
) {
    if (rideWithLocs == null) {
        // still loading
        Box(
            modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val ride = rideWithLocs.bikeRideEnt
    val path: List<LatLng> = rideWithLocs.locations.map { LatLng(it.lat, it.lng) }

    // build bounds if we have multiple points
    val bounds = remember(path) {
        if (path.size > 1) {
            LatLngBounds.builder().apply {
                path.forEach { include(it) }
            }.build()
        } else null
    }

    // camera starts at first point (or a fallback)
    val cameraPositionState = rememberCameraPositionState {
        position = path.firstOrNull()
            ?.let { CameraPosition.fromLatLngZoom(it, 15f) }
            ?: CameraPosition.fromLatLngZoom(
                LatLng(ride.startLat, ride.startLng), 12f
            )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Ride Detail", style = MaterialTheme.typography.titleLarge)

        // Metrics row
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Distance: %.1f km".format(ride.totalDistance / 1000f))
            Text("Avg: %.1f km/h".format(ride.averageSpeed))
            Text("Max: %.1f km/h".format(ride.maxSpeed))
        }

        // The map preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            GoogleMap(
                modifier             = Modifier.matchParentSize(),
                cameraPositionState  = cameraPositionState,
                uiSettings           = MapUiSettings(zoomControlsEnabled = true)
            ) {
                if (path.isNotEmpty()) {
                    Polyline(points = path, color = MaterialTheme.colorScheme.primary, width = 6f)
                    Marker(state = MarkerState(path.first()), title = "Start")
                    Marker(state = MarkerState(path.last()),  title = "End")
                }
            }

            // once bounds are known, move the camera to fit them
            LaunchedEffect(bounds) {
                bounds?.let {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngBounds(it, /* paddingPx = */ 50)
                    )
                }
            }
        }

        // Any additional detail (notes, weather, etc.)
        ride.notes?.let {
            Text("Notes: $it", style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Preview
@Composable
fun RideDetailScreenPreview() {
    val bikeRide = BikeRideEntity(
        startTime = 1678886400000L, // Example timestamp
        endTime = 1678890000000L,
        totalDistance = 15000f,
        averageSpeed = 25f,
        maxSpeed = 35f,
        elevationGain = 200f,
        elevationLoss = 150f,
        caloriesBurned = 500,
        avgHeartRate = 140,
        maxHeartRate = 160,
        weatherCondition = "Sunny",
        rideType = "Commute",
        notes = "Great ride!",
        rating = 5,
        bikeId = "Bike123",
        batteryStart = 80,
        batteryEnd = 60,
        startLat = 37.7749,
        startLng = -122.4194,
        endLat = 37.7850,
        endLng = -122.4060
    )

    val path = listOf(
        LatLng(bikeRide.startLat, bikeRide.startLng),
        LatLng(bikeRide.endLat,   bikeRide.endLng)
    )
    val centerLat = (bikeRide.startLat + bikeRide.endLat) / 2
    val centerLng = (bikeRide.startLng + bikeRide.endLng) / 2

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(centerLat, centerLng),
            13f
        )
    }

    GoogleMap(
        modifier             = Modifier.fillMaxSize(),
        cameraPositionState  = cameraState,
        uiSettings           = MapUiSettings(zoomControlsEnabled = true),
        properties           = MapProperties(mapType = MapType.NORMAL)
    ) {
        Polyline(points = path, width = 5f)
        Marker(state = MarkerState(path.first()), title = "Start")
        Marker(state = MarkerState(path.last()),  title = "End")    }
}
