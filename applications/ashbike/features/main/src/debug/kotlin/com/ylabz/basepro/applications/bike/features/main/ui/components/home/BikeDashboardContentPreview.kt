package com.ylabz.basepro.applications.bike.features.main.ui.components.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.ui.theme.AshBikeTheme // Assuming theme location
import kotlinx.collections.immutable.persistentMapOf

@Preview(showBackground = true, name = "Bike Dashboard Content - Connected")
@Composable
fun BikeDashboardContentPreviewConnected() {
    val dummyBikeRideInfo = BikeRideInfo(
        location = LatLng(37.4219999, -122.0862462),
        currentSpeed = 25.5,
        averageSpeed = 18.2,
        maxSpeed = 40.0,
        currentTripDistance = 10.5f,
        totalTripDistance = 20.0f,
        remainingDistance = null,
        elevationGain = 120.0,
        elevationLoss = 30.0,
        caloriesBurned = 350,
        rideDuration = "00:45:30",
        settings = persistentMapOf(),
        heading = 90f,
        elevation = 150.0,
        isBikeConnected = true,
        batteryLevel = 85,
        motorPower = 250f,
        rideState = RideState.Riding,
        bikeWeatherInfo = null,
        heartbeat = 78
    )
    val uiState = BikeUiState.Success(
        dummyBikeRideInfo
    )

    AshBikeTheme {
        BikeDashboardContent(
            uiState = uiState,
            onBikeEvent = { },
            navTo = { }
        )
    }
}

@Preview(showBackground = true, name = "Bike Dashboard Content - Disconnected")
@Composable
fun BikeDashboardContentPreviewDisconnected() {
    val dummyBikeRideInfo = BikeRideInfo(
        location = LatLng(37.4219999, -122.0862462),
        currentSpeed = 0.0,
        averageSpeed = 0.0,
        maxSpeed = 0.0,
        currentTripDistance = 0.0f,
        totalTripDistance = null,
        remainingDistance = null,
        elevationGain = 0.0,
        elevationLoss = 0.0,
        caloriesBurned = 0,
        rideDuration = "00:00",
        settings = persistentMapOf(),
        heading = 0f,
        elevation = 0.0,
        isBikeConnected = false,
        batteryLevel = null,
        motorPower = null,
        rideState = RideState.NotStarted,
        bikeWeatherInfo = null,
        heartbeat = null
    )
    val uiState = BikeUiState.Success(dummyBikeRideInfo)

    AshBikeTheme {
        BikeDashboardContent(
            uiState = uiState,
            onBikeEvent = { },
            navTo = { }
        )
    }
}
