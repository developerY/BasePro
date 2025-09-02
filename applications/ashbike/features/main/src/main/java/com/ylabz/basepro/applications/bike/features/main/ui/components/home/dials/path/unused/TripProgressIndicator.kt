package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path.unused

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp

@Composable
fun TripProgressIndicator(
    currentDistance: Double,
    totalDistance: Double,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 12.dp
) {
    val progressFraction = if (totalDistance > 0) {
        (currentDistance / totalDistance).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    BoxWithConstraints(
        modifier = modifier
            // .height(...) is provided externally (e.g., 80.dp)
            .padding(horizontal = 16.dp)
    ) {
        val bc = this
        // Make the bike icon bigger
        val bikeIconSize = 64.dp

        // The track area is maxWidth - bikeIconSize, so the icon doesn’t clip
        val iconHalf = bikeIconSize / 2
        val trackWidth = bc.maxWidth - bikeIconSize
        val iconOffset = trackWidth * progressFraction

        // Draw the horizontal track
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .align(Alignment.Center)
                .padding(start = iconHalf, end = iconHalf)
        ) {
            // Full track
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = size.height
            )
            // Progress portion
            drawLine(
                color = Color(0xFF90CAF9),
                start = Offset(0f, size.height / 2),
                end = Offset(size.width * progressFraction, size.height / 2),
                strokeWidth = size.height
            )
        }

        // Bike icon
        Icon(
            imageVector = Icons.Filled.DirectionsBike,
            contentDescription = "Trip Progress",
            tint = Color.Red,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = iconOffset + iconHalf)
                .size(bikeIconSize)
        )
    }
}


/*
@Preview
@Composable
fun TripProgressIndicatorPreview() {
    val currentDistance = 5.0 // Current distance traveled
    TripProgressIndicator(currentDistance = currentDistance, totalDistance = 10.0)
}
*/