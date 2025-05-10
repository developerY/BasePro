package com.ylabz.basepro.applications.bike.features.main.ui.components.home.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.SpeedometerWithCompassOverlay
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.weather.WeatherBadgeWithDetails
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path.BikePathWithControls
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import com.ylabz.basepro.feature.weather.ui.components.combine.WindDirectionDialWithSpeed


@Composable
fun SpeedAndProgressCard(
    modifier: Modifier = Modifier.fillMaxSize(),
    bikeRideInfo: BikeRideInfo,

    /*currentSpeed: Double,
    currentTripDistance: Float,
    totalDistance: Float?,
    windDegree: Float,
    windSpeed: Float,
    weatherConditionText: String,
    heading: Float,*/

    onBikeEvent: (BikeEvent) -> Unit,
    /*isRiding: Boolean,                               // <--- Added to track if ride is running or paused
    onStartPauseClicked: () -> Unit,                 // <--- Callback for Start/Pause
    onStopClicked: () -> Unit*/                    // <--- Callback for Stop

    navTo: (String) -> Unit,

    ) {
    // Control the visibility of the wind dial & weather badge for a subtle fade/slide in
    var showOverlays by remember { mutableStateOf(false) }
    // Trigger the overlays to appear after composition
    LaunchedEffect(Unit) {
        showOverlays = true
    }

    // Extract values from bikeRideInfo (if needed)
    val currentSpeed = bikeRideInfo.currentSpeed
    val heading: Float = bikeRideInfo.heading

    val weather = bikeRideInfo.bikeWeatherInfo



    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor =
            if (bikeRideInfo.rideState == RideState.Riding)
                Color(0xFF1976D2)
            else
                Color.Gray


        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP ROW: wind + weather
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Wind dial
                AnimatedVisibility(
                    visible = showOverlays,
                    enter = fadeIn(animationSpec = tween(600)) +
                            slideInHorizontally(initialOffsetX = { -it / 2 }, animationSpec = tween(600))
                ) {
                    Box(modifier = Modifier.size(60.dp)) {
                        weather?.let {
                            WindDirectionDialWithSpeed(degree = it.windDegree, speed = it.windSpeed)
                        }
                    }
                }

                // Weather badge
                AnimatedVisibility(
                    visible = showOverlays,
                    enter = fadeIn(animationSpec = tween(600)) +
                            slideInHorizontally(initialOffsetX = { it / 2 }, animationSpec = tween(600))
                ) {
                    weather?.let {
                        WeatherBadgeWithDetails(
                            weatherInfo = it
                        )
                    }
                }
            }

            // MIDDLE: Large speedometer (responsive)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // We measure the parent width, pick a responsive size for the speedometer
                BoxWithConstraints {
                    val boxWithConstraintsScope = this
                    val availableWidth = boxWithConstraintsScope.maxWidth
                    val gaugeSize = availableWidth.coerceAtMost(450.dp)
                    SpeedometerWithCompassOverlay(
                        currentSpeed = currentSpeed.toFloat(),
                        maxSpeed = 60f,
                        heading = heading,
                        modifier = Modifier.size(gaugeSize)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                //TripControlsWithProgress
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
        onBikeEvent = {

        },
        navTo = {
            //navigation
        }

    )
}














