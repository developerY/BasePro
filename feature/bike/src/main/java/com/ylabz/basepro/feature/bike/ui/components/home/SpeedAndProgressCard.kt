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
import kotlin.math.roundToLong

@Composable
fun SpeedAndProgressCard(
    currentSpeed: Double,
    currentTripDistance: Double,
    totalDistance: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            // Increase the height so speedometer and progress line don’t overlap
            .height(320.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2))
    ) {
        // We use a Column that splits the card vertically
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1) Speedometer in the top half
            Box(
                modifier = Modifier
                    .weight(1f) // take half (or more) of the vertical space
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                FancySpeedometer(
                    currentSpeed = currentSpeed.toFloat(),
                    maxSpeed = 60f,
                    modifier = Modifier.size(220.dp) // speedometer size
                )
            }

            // 2) A spacer to separate them
            //Spacer(modifier = Modifier.height(16.dp))

            // 3) Trip progress indicator near the bottom
            Box(
                modifier = Modifier
                    .weight(0.5f) // allocate some vertical space for the progress line
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BigBikeProgressIndicator(
                    currentDistance = currentTripDistance,
                    totalDistance = totalDistance,
                    iconSize = 64.dp,
                    containerHeight = 120.dp,  // internal height for the indicator
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
        modifier = Modifier
    )
}

