package com.ylabz.basepro.applications.bike.features.main.ui.components.home.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.SpeedometerWithCompassOverlay
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path.BikePathWithControls
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.weather.WeatherBadgeWithDetails
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import com.ylabz.basepro.feature.weather.ui.components.combine.WindDirectionDialWithSpeed

@Composable
fun SpeedAndProgressCard(
    modifier: Modifier = Modifier.fillMaxSize(),
    bikeRideInfo: BikeRideInfo,
    onBikeEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit,
) {
    var weatherIconsVisible by remember { mutableStateOf(false) }

    val currentSpeed = bikeRideInfo.currentSpeed
    val heading: Float = bikeRideInfo.heading
    val weather = bikeRideInfo.bikeWeatherInfo

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (bikeRideInfo.rideState == RideState.Riding) Color(0xFF1976D2) else Color.Gray
        )
    ) {
        // Using a Box as the root layout resolves the scope issue.
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Speedometer is the background, always at max size
            SpeedometerWithCompassOverlay(
                currentSpeed = currentSpeed.toFloat(),
                maxSpeed = 60f,
                heading = heading,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )

            // Tappable weather icon, aligned to the top end
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Toggle Weather",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(24.dp)
                    .clickable { weatherIconsVisible = !weatherIconsVisible }
            )

            // CORRECTED: This AnimatedVisibility is now called inside a Box,
            // which does not cause the scope error.
            this@Card.AnimatedVisibility(
                visible = weatherIconsVisible,
                modifier = Modifier.align(Alignment.TopStart),
                enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -it }),
                exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it })
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(48.dp)
                ) {
                    weather?.let {
                        WindDirectionDialWithSpeed(degree = it.windDegree, speed = it.windSpeed)
                    }
                }
            }

            // CORRECTED: This AnimatedVisibility is also correctly placed in the Box scope.
            this@Card.AnimatedVisibility(
                visible = weatherIconsVisible,
                modifier = Modifier.align(Alignment.TopEnd),
                enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it }),
                exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { it })
            ) {
                Box(modifier = Modifier.padding(top = 48.dp, end = 16.dp)) {
                    weather?.let {
                        WeatherBadgeWithDetails(weatherInfo = it)
                    }
                }
            }

            // Bottom controls, aligned to the bottom center
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                BikePathWithControls(
                    bikeRideInfo = bikeRideInfo,
                    onBikeEvent = onBikeEvent,
                )
            }
        }
    }
}