package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
            // Give enough vertical space for a large speedometer + progress bar
            .height(400.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2))
    ) {
        // Column that arranges:
        // 1) top row (wind dial + weather badge),
        // 2) center speedometer,
        // 3) bottom progress line
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP ROW: small wind dial (left) + weather badge (right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Small wind dial on the left
                Box(modifier = Modifier.size(50.dp)) {
                    WindDirectionDialWithSpeed(
                        degree = windDegree,
                        speed = windSpeed
                    )
                }
                // Weather badge on the right
                WeatherBadge(conditionText = weatherConditionText)
            }

            // MIDDLE: Large speedometer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),  // Take remaining vertical space
                contentAlignment = Alignment.Center
            ) {
                // Make the speedometer as large as possible within 400dp card
                FancySpeedometer(
                    currentSpeed = currentSpeed.toFloat(),
                    maxSpeed = 60f,
                    modifier = Modifier.size(340.dp)  // Very large speedometer
                )
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
                    iconSize = 54.dp,
                    containerHeight = 50.dp,
                    trackHeight = 12.dp
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
        currentTripDistance = 10.0,
        totalDistance = 50.0,
        windDegree = 120f,
        windSpeed = 5.0f,
        weatherConditionText = WeatherConditionUnif.RAINY.name,
        modifier = Modifier
    )
}

