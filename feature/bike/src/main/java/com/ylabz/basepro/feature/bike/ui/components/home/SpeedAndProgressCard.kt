package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
            .height(200.dp)
            .shadow(8.dp, shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Center a column for speed text & trip progress line
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),  // side padding if you want
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Large Speed Text
                Text(
                    text = "${currentSpeed.roundToLong()} km/h",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp
                    ),
                    color = Color.White
                )

                // Trip progress line with bike icon
                // Adjust trackHeight, icon size, or container height as needed
                TripProgressIndicator(
                    currentDistance = currentTripDistance,
                    totalDistance = totalDistance,
                    trackHeight = 6.dp,  // smaller thickness
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

