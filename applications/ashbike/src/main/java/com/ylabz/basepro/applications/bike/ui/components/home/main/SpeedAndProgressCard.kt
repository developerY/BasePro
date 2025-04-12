package com.ylabz.basepro.applications.bike.ui.components.home.main

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.ui.components.home.dials.SpeedometerWithCompassOverlay
import com.ylabz.basepro.applications.bike.ui.components.home.dials.WeatherBadge
import com.ylabz.basepro.applications.bike.ui.components.path.BigBikeProgressIndicator
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif
import com.ylabz.basepro.feature.weather.ui.components.combine.WindDirectionDialWithSpeed

@Composable
fun SpeedAndProgressCard(
    modifier: Modifier = Modifier.fillMaxSize(),
    currentSpeed: Double,
    currentTripDistance: Float,
    totalDistance: Float,
    windDegree: Float,
    windSpeed: Float,
    weatherConditionText: String,
    heading: Float,
) {
    // Control the visibility of the wind dial & weather badge for a subtle fade/slide in
    var showOverlays by remember { mutableStateOf(false) }
    // Trigger the overlays to appear after composition
    LaunchedEffect(Unit) {
        showOverlays = true
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            // Enough height for large speedometer, progress bar, small wind dial
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
            // TOP ROW: small wind dial (left) + weather badge (right) with animations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 1) Wind dial, animated
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

                // 2) Weather badge, animated
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
                    // Use a fraction of availableWidth or clamp it to some range
                    val gaugeSize = availableWidth.coerceAtMost(450.dp) // up to 340dp
                    SpeedometerWithCompassOverlay(
                        currentSpeed = currentSpeed.toFloat(),
                        maxSpeed = 60f,
                        heading = heading,
                        modifier = Modifier.size(gaugeSize)
                    )
                }
            }

            // BOTTOM: Trip progress line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                BigBikeProgressIndicator(
                    currentDistance = currentTripDistance,
                    totalDistance = totalDistance,
                    iconSize = 48.dp,
                    containerHeight = 60.dp,
                    trackHeight = 8.dp
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
        modifier = Modifier
    )
}

