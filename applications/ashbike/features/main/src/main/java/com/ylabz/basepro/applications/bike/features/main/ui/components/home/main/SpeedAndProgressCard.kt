package com.ylabz.basepro.applications.bike.features.main.ui.components.home.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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

    val speedometerSizeFraction by animateFloatAsState(
        targetValue = if (weatherIconsVisible) 0.8f else 1.0f,
        animationSpec = tween(durationMillis = 400),
        label = "speedometerSize"
    )

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP ROW: Wind, a spacer, and Weather
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp), // Fixed height for this row
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Wind dial
                AnimatedVisibility(
                    visible = weatherIconsVisible,
                    enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -it / 2 }),
                    exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it / 2 })
                ) {
                    Box(modifier = Modifier.size(60.dp)) {
                        weather?.let {
                            WindDirectionDialWithSpeed(degree = it.windDegree, speed = it.windSpeed)
                        }
                    }
                }

                // Main Weather Icon (always visible)
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Toggle Weather",
                    tint = Color.White,
                    modifier = Modifier
                        .clickable { weatherIconsVisible = !weatherIconsVisible }
                        .padding(8.dp)
                        .size(36.dp)
                )

                // Weather badge
                AnimatedVisibility(
                    visible = weatherIconsVisible,
                    enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it / 2 }),
                    exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { it / 2 })
                ) {
                    weather?.let {
                        WeatherBadgeWithDetails(weatherInfo = it)
                    }
                }
            }

            // Middle: Large speedometer (responsive and animated)
            Box(
                modifier = Modifier
                    .weight(1f) // Takes up the remaining space
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                SpeedometerWithCompassOverlay(
                    currentSpeed = currentSpeed.toFloat(),
                    maxSpeed = 60f,
                    heading = heading,
                    modifier = Modifier.fillMaxSize(speedometerSizeFraction) // Apply animated fraction here
                )
            }

            // Bottom: Controls
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
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

@Preview
@Composable
fun SpeedAndProgressCardPreview() {
    val bikeRideInfo = BikeRideInfo(
        location = null,
        currentSpeed = 25.0,
        averageSpeed = 20.0,
        maxSpeed = 40.0,
        currentTripDistance = 1000.0f,
        totalTripDistance = 5000.0f,
        remainingDistance = 4000.0f,
        elevationGain = 100.0,
        elevationLoss = 50.0,
        caloriesBurned = 300,
        rideDuration = "00:30:00",
        settings = mapOf("speed_unit" to listOf("kmh"), "distance_unit" to listOf("km")),
        heading = 90.0f,
        elevation = 150.0,
        isBikeConnected = true,
        batteryLevel = 80,
        motorPower = 50.0f,
        rideState = RideState.Riding,
        bikeWeatherInfo = BikeWeatherInfo(
            windDegree = 12,
            windSpeed = 12.3,
            conditionText = "Sunny",
            conditionDescription = "Clear skies",
            conditionIcon = "",
            temperature = 25.0,
            humidity = 12,
            feelsLike = 23.0
        )
    )

    /*
        weatherCondition = "",
        weatherConditionText = "Sunny",
        windSpeed = 10.0,
        windDegree = 180.0,
        temperature = 25.0,
        feelsLike = 23.0,

        public final val windDegree: Int,
        public final val windSpeed: Double,
        public final val conditionText: String,
        public final val conditionDescription: String?,
        public final val conditionIcon: String?,
        public final val temperature: Double?,
        public final val feelsLike: Double?,
        public final val humidity: Int?
 */


    SpeedAndProgressCard(
        bikeRideInfo = bikeRideInfo,
        onBikeEvent = {},
        navTo = {}
    )
}