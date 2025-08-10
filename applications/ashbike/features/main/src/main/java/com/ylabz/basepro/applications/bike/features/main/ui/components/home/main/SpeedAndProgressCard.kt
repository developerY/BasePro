package com.ylabz.basepro.applications.bike.features.main.ui.components.home.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng // Needed for BikeRideInfo in preview
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState // Added import
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.GpsLevelIndicator
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.SpeedometerWithCompassOverlay
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path.BikePathWithControls
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.weather.WeatherBadgeWithDetails
import com.ylabz.basepro.core.model.bike.BikeRideInfo // Added import
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import com.ylabz.basepro.feature.weather.ui.components.combine.WindDirectionDialWithSpeed
import kotlinx.collections.immutable.persistentMapOf // Needed for BikeRideInfo in preview

@Composable
fun SpeedAndProgressCard(
    modifier: Modifier = Modifier.fillMaxSize(),
    uiState: BikeUiState.Success, // Changed parameter
    onBikeEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit,
    containerColor: Color,
    contentColor: Color,
) {
    var weatherIconsVisible by remember { mutableStateOf(false) }

    val bikeData = uiState.bikeData // Access bikeData from uiState
    val currentSpeed = bikeData.currentSpeed
    val heading = bikeData.heading
    val weather = bikeData.bikeWeatherInfo

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        val cardScope = this

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            SpeedometerWithCompassOverlay(
                currentSpeed = currentSpeed.toFloat(),
                maxSpeed = 60f, // Consider making this dynamic if needed
                heading = heading,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentColor = contentColor
            )

            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Toggle Weather",
                tint = contentColor.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .size(24.dp)
                    .clickable { weatherIconsVisible = !weatherIconsVisible }
            )

            GpsLevelIndicator(
                uiState = uiState,
                onEvent = onBikeEvent, // <<< MODIFIED HERE
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 16.dp)
            )

            cardScope.AnimatedVisibility(
                visible = weatherIconsVisible,
                modifier = Modifier.align(Alignment.TopStart),
                enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -it }),
                exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it })
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(48.dp) // Consider adjusting size if necessary
                ) {
                    weather?.let {
                        WindDirectionDialWithSpeed(degree = it.windDegree, speed = it.windSpeed)
                    }
                }
            }

            cardScope.AnimatedVisibility(
                visible = weatherIconsVisible,
                modifier = Modifier.align(Alignment.TopEnd),
                enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it }),
                exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { it })
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    weather?.let {
                        WeatherBadgeWithDetails(weatherInfo = it)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                BikePathWithControls(
                    uiState = uiState, // Just pass the state object
                    onBikeEvent = onBikeEvent,
                    //navTo = navTo
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 500)
@Composable
fun FinalSpeedometerCardPreview() {
    val sampleBikeData = BikeRideInfo( // Updated preview data
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
    val sampleUiState = BikeUiState.Success(sampleBikeData) // Wrapped in Success state

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A)), // Dark background for contrast
            contentAlignment = Alignment.Center
        ) {
            SpeedAndProgressCard(
                modifier = Modifier.padding(16.dp),
                uiState = sampleUiState, // Pass uiState
                onBikeEvent = { },
                navTo = { },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
