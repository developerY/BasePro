package com.ylabz.basepro.applications.bike.ui.components.home.main

import android.R.attr.maxWidth
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.ui.BikeEvent
import com.ylabz.basepro.applications.bike.ui.components.home.dials.SpeedometerWithCompassOverlay
import com.ylabz.basepro.applications.bike.ui.components.home.dials.WeatherBadge
import com.ylabz.basepro.applications.bike.ui.components.path.BigBikeProgressIndicator
import com.ylabz.basepro.applications.bike.ui.components.path.BikePathWithControls
import com.ylabz.basepro.applications.bike.ui.components.path.TripControlsWithProgress
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif
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
    val currentTripDistance = bikeRideInfo.currentTripDistance
    val totalDistance = bikeRideInfo.totalTripDistance
    val tripDuration = bikeRideInfo.rideDuration
    val averageSpeed = bikeRideInfo.averageSpeed
    val elevation = bikeRideInfo.elevation
    val heading: Float = bikeRideInfo.heading
    //val isRiding = bikeRideInfo.isRiding

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2))
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
                        WindDirectionDialWithSpeed(
                            degree = windDegree,
                            speed = windSpeed
                        )
                    }
                }

                // Weather badge
                AnimatedVisibility(
                    visible = showOverlays,
                    enter = fadeIn(animationSpec = tween(600)) +
                            slideInHorizontally(initialOffsetX = { it / 2 }, animationSpec = tween(600))
                ) {
                    WeatherBadge(conditionText = weatherConditionText)
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
                    val availableWidth = maxWidth
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
                    currentDistance = currentTripDistance,
                    totalDistance = totalDistance,
                    isRiding = isRiding,
                    onStartPauseClicked = onStartPauseClicked,
                    onStopClicked = onStopClicked
                )
            }

        }
    }
}

@Preview
@Composable
fun SpeedAndProgressCardPreview() {
    SpeedAndProgressCard(
        currentSpeed = 25.5,
        currentTripDistance = 10.0f,
        totalDistance = 50.0f,
        windDegree = 120f,
        windSpeed = 5.0f,
        weatherConditionText = WeatherConditionUnif.RAINY.name,
        heading = 45f,
        isRiding = false,  // Just for preview
        onStartPauseClicked = {},
        onStopClicked = {}
    )
}


