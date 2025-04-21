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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import com.ylabz.basepro.applications.bike.database.mapper.BikeRide

@Composable
fun RideDetailScreen(
    modifier: Modifier = Modifier,
    ride:     BikeRide?
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            ride == null -> {
                CircularProgressIndicator()
            }
            else -> {
                // Build your path
                val path = listOf(
                    LatLng(ride.startLat, ride.startLng),
                    LatLng(ride.endLat,   ride.endLng)
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
                    modifier            = Modifier.fillMaxSize(),
                    cameraPositionState = cameraState,
                    uiSettings          = MapUiSettings(zoomControlsEnabled = true),
                    properties          = MapProperties(mapType = MapType.NORMAL)
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
