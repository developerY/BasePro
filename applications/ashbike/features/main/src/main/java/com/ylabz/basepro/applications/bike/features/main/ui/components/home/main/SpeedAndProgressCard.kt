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
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.SpeedometerWithCompassOverlay
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path.BikePathWithControls
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.weather.WeatherBadgeWithDetails
// import com.ylabz.basepro.core.model.bike.BikeRideInfo // No longer needed
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import com.ylabz.basepro.feature.weather.ui.components.combine.WindDirectionDialWithSpeed

@Composable
fun SpeedAndProgressCard(
    modifier: Modifier = Modifier.fillMaxSize(),
    getCurrentSpeed: () -> Double,
    getHeading: () -> Float,
    getBikeWeatherInfo: () -> BikeWeatherInfo?,
    getRideState: () -> RideState,
    getCurrentTripDistance: () -> Float,
    getTotalTripDistance: () -> Float?,
    onBikeEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit,
    containerColor: Color,
    contentColor: Color,
) {
    var weatherIconsVisible by remember { mutableStateOf(false) }

    val currentSpeed = getCurrentSpeed()
    val heading = getHeading()
    val weather = getBikeWeatherInfo()

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
                    getRideState = getRideState,
                    getCurrentTripDistance = getCurrentTripDistance,
                    getTotalTripDistance = getTotalTripDistance,
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
    // Sample data for previewing
    val sampleCurrentSpeed = 42.5
    val sampleHeading = 292f
    val sampleWeatherInfo = BikeWeatherInfo(
        windDegree = 45,
        windSpeed = 15.0,
        conditionText = "Sunny",
        conditionDescription = "Clear sky",
        conditionIcon = "01d",
        temperature = 22.0,
        feelsLike = 21.0,
        humidity = 60
    )
    val sampleRideState = RideState.Riding
    val sampleCurrentTripDistance = 12.5f
    val sampleTotalTripDistance = 20.0f

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A)), // Dark background for contrast
            contentAlignment = Alignment.Center
        ) {
            SpeedAndProgressCard(
                modifier = Modifier.padding(16.dp),
                getCurrentSpeed = { sampleCurrentSpeed },
                getHeading = { sampleHeading },
                getBikeWeatherInfo = { sampleWeatherInfo },
                getRideState = { sampleRideState },
                getCurrentTripDistance = { sampleCurrentTripDistance },
                getTotalTripDistance = { sampleTotalTripDistance },
                onBikeEvent = { },
                navTo = { },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
