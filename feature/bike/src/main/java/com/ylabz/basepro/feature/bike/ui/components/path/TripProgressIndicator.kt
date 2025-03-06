package com.ylabz.basepro.feature.bike.ui.components.path

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TripProgressIndicator(
    currentDistance: Double,
    totalDistance: Double,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 6.dp
) {
    val progressFraction = if (totalDistance > 0) {
        (currentDistance / totalDistance).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)  // Enough vertical space for the icon
    ) {
        // Draw the horizontal progress track
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .align(Alignment.Center)
        ) {
            // Full track (light gray)
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = size.height
            )
            // Progress portion (blue)
            drawLine(
                color = Color(0xFF90CAF9),
                start = Offset(0f, size.height / 2),
                end = Offset(size.width * progressFraction, size.height / 2),
                strokeWidth = size.height
            )
        }

        // Bike icon offset
        val bikeIconSize = 24.dp
        val iconOffset = maxWidth * progressFraction

        Icon(
            imageVector = Icons.Filled.DirectionsBike,
            contentDescription = "Trip Progress",
            tint = Color.Red,
            modifier = Modifier
                .align(Alignment.CenterStart)
                // offset by half the icon's width so it sits on the line
                .offset(x = iconOffset - bikeIconSize / 2)
                .size(bikeIconSize)
        )
    }
}


@Preview
@Composable
fun TripProgressIndicatorPreview() {
    val currentDistance = 5.0 // Current distance traveled
    TripProgressIndicator(currentDistance = currentDistance, totalDistance = 10.0)
}
