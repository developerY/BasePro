package com.ylabz.basepro.applications.bike.features.main.ui.components.home.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import kotlinx.collections.immutable.persistentMapOf

@Preview(showBackground = true, widthDp = 380, heightDp = 500)
@Composable
fun FinalSpeedometerCardPreview() {
    val sampleBikeData = BikeRideInfo(
        location = LatLng(37.4219999, -122.0862462),
        currentSpeed = 42.5,
        averageSpeed = 30.0,
        maxSpeed = 55.0,
        currentTripDistance = 12.5f,
        totalTripDistance = 20.0f,
        remainingDistance = 7.5f,
        elevationGain = 100.0,
        elevationLoss = 20.0,
        caloriesBurned = 250,
        rideDuration = "00:30:00",
        settings = persistentMapOf(),
        heading = 292f,
        elevation = 50.0,
        isBikeConnected = true,
        batteryLevel = 75,
        motorPower = 250f,
        rideState = RideState.Riding,
        bikeWeatherInfo = BikeWeatherInfo(
            windDegree = 45,
            windSpeed = 15.0,
            conditionText = "Sunny",
            conditionDescription = "Clear sky",
            conditionIcon = "01d",
            temperature = 22.0,
            feelsLike = 21.0,
            humidity = 60,
        ),
        heartbeat = null,
    )
    val sampleUiState = BikeUiState.Success(sampleBikeData)

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A)), 
            contentAlignment = Alignment.Center
        ) {
            SpeedAndProgressCard(
                modifier = Modifier.padding(16.dp),
                uiState = sampleUiState,
                onBikeEvent = { },
                navTo = { },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onShowMapPanel = { }
            )
        }
    }
}
