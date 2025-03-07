package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ylabz.basepro.feature.bike.ui.components.path.BigBikeProgressIndicator
import com.ylabz.basepro.feature.bike.ui.components.path.TripProgressIndicator
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif
import com.ylabz.basepro.feature.weather.ui.components.combine.WindDirectionDialWithSpeed
import kotlin.math.roundToLong

@Composable
fun SpeedAndProgressCard(
    currentSpeed: Double,
    currentTripDistance: Double,
    totalDistance: Double,
    windDegree: Float,
    windSpeed: Float,
    weatherConditionText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp), // increased height so there's enough space
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2))
    ) {
        // We use a Box to layer items:
        // 1) Speedometer & progress line in the center
        // 2) Wind dial top-left
        // 3) Weather condition top-right
        Box(modifier = Modifier.fillMaxSize()) {

            // The main column for speedometer and progress line
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Speedometer
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    FancySpeedometer(
                        currentSpeed = currentSpeed.toFloat(),
                        maxSpeed = 60f,
                        modifier = Modifier.size(220.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Trip progress line
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    BigBikeProgressIndicator(
                        currentDistance = currentTripDistance,
                        totalDistance = totalDistance,
                        iconSize = 64.dp,
                        containerHeight = 120.dp,
                        trackHeight = 12.dp
                    )
                }
            }

            // 1) Wind dial in the top-left corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(80.dp), // adjust for your dial size
                contentAlignment = Alignment.Center
            ) {
                WindDirectionDialWithSpeed(
                    degree = windDegree,
                    speed = windSpeed
                )
            }

            // 2) Weather condition badge in the top-right corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                WeatherConditionBadge(conditionText = weatherConditionText)
            }
        }
    }
}

/**
 * A simple composable that displays the weather condition text
 * in a small styled container.
 */
@Composable
fun WeatherConditionBadge(
    conditionText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
    ) {
        Text(
            text = conditionText,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Black,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}


@Preview
@Composable
fun SpeedAndProgressCardPreview() {
    SpeedAndProgressCard(
        currentSpeed = 25.5,
        currentTripDistance = 10.0,
        totalDistance = 50.0,
        windDegree = 120f,
        windSpeed = 5.0f,
        weatherConditionText = WeatherConditionUnif.RAINY.name,
        modifier = Modifier
    )
}

