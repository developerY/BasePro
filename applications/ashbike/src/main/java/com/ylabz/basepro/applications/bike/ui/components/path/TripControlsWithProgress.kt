package com.ylabz.basepro.applications.bike.ui.components.path

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.ui.components.home.dials.SpeedometerWithCompassOverlay
import com.ylabz.basepro.applications.bike.ui.components.home.dials.WeatherBadge
import com.ylabz.basepro.applications.bike.ui.components.path.BigBikeProgressIndicator
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif
import com.ylabz.basepro.feature.weather.ui.components.combine.WindDirectionDialWithSpeed


@Composable
fun TripControlsWithProgress(
    currentDistance: Float,
    totalDistance: Float?,
    isRiding: Boolean,
    onStartPauseClicked: () -> Unit,
    onStopClicked: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 48.dp,
    containerHeight: Dp = 60.dp,
    trackHeight: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(containerHeight)
    ) {
        // The bike progress indicator is at the center background of this box
        BigBikeProgressIndicator(
            currentDistance = currentDistance,
            totalDistance = totalDistance,
            iconSize = iconSize,
            containerHeight = containerHeight,
            trackHeight = trackHeight
        )

        // Place the Start/Pause and Stop buttons on top, one on each side.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center) // Aligns the row in the middle of the box
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: Start or Pause FAB
            FloatingActionButton(
                onClick = onStartPauseClicked,
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                val icon = if (isRiding) Icons.Default.Pause else Icons.Default.PlayArrow
                val desc = if (isRiding) "Pause" else "Start"
                Icon(imageVector = icon, contentDescription = desc)
            }

            // RIGHT: Stop FAB
            FloatingActionButton(
                onClick = onStopClicked,
                containerColor = Color.White,
                contentColor = Color.Red
            ) {
                Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop")
            }
        }
    }
}

@Preview
@Composable
fun TripControlsWithProgressPreview() {
    TripControlsWithProgress(
        currentDistance = 0f,
        totalDistance = 2000f,
        isRiding = true,
        onStartPauseClicked = {},
        onStopClicked = {}
    )
}
