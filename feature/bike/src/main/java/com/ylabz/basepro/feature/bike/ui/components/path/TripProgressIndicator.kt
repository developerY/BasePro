package com.ylabz.basepro.feature.bike.ui.components.path

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
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
    currentDistance: Double, // e.g., current trip distance in km
    totalDistance: Double,   // planned trip distance in km
    modifier: Modifier = Modifier,
    trackHeight: Dp = 4.dp
) {
    // Calculate the progress as a fraction [0f, 1f].
    val progressFraction = if (totalDistance > 0) {
        (currentDistance / totalDistance).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    BoxWithConstraints(modifier = modifier.fillMaxWidth().height(40.dp)) {
        // Draw the horizontal progress track.
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(trackHeight)
            .align(Alignment.Center)) {
            // Full-length track (background)
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = size.height
            )
            // Draw progress line up to current progress.
            drawLine(
                color = Color(0xFF1976D2),
                start = Offset(0f, size.height / 2),
                end = Offset(size.width * progressFraction, size.height / 2),
                strokeWidth = size.height
            )
        }

        // Place the bike icon at the current progress point.
        // We use BoxWithConstraints to get maxWidth in Dp.
        val bikeIconSize = 52.dp
        // Calculate the offset in Dp based on the progressFraction.
        val iconOffset = maxWidth * progressFraction

        Icon(
            imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
            contentDescription = "Trip Progress",
            tint = Color.Red,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = iconOffset - bikeIconSize / 2)  // Center the icon on the progress point.
                .then(Modifier)
                .height(bikeIconSize)
        )
    }
}

@Preview
@Composable
fun TripProgressIndicatorPreview() {
    val currentDistance = 5.0 // Current distance traveled
    TripProgressIndicator(currentDistance = currentDistance, totalDistance = 10.0)
}
