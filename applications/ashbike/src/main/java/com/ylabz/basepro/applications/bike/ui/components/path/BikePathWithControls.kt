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
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.ylabz.basepro.applications.bike.ui.BikeEvent
import com.ylabz.basepro.applications.bike.ui.components.home.dials.SpeedometerWithCompassOverlay
import com.ylabz.basepro.applications.bike.ui.components.home.dials.WeatherBadge
import com.ylabz.basepro.applications.bike.ui.components.path.BigBikeProgressIndicator
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif
import com.ylabz.basepro.feature.weather.ui.components.combine.WindDirectionDialWithSpeed


@Composable
fun BikePathWithControls(
    modifier: Modifier = Modifier,
    bikeRideInfo: BikeRideInfo,
    onBikeEvent: (BikeEvent) -> Unit,
    iconSize: Dp = 48.dp,
    trackHeight: Dp = 8.dp,
    buttonSize: Dp = 60.dp
) {
    val currentDistance = bikeRideInfo.currentTripDistance
    val totalDistance = bikeRideInfo.totalTripDistance
    // Riding whenever state == Riding
    val isRiding = bikeRideInfo.rideState == RideState.Riding

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(buttonSize)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Start / Pause button
        FloatingActionButton(
            onClick = { onBikeEvent(BikeEvent.StartPauseRide) },
            containerColor = Color.White,
            contentColor = Color.Black,
            modifier = Modifier.size(buttonSize)
        ) {
            val icon = if (isRiding) Icons.Default.Pause else Icons.Default.PlayArrow
            val desc = if (isRiding) "Pause" else "Start"
            Icon(imageVector = icon, contentDescription = desc)
        }

        // Progress indicator in middle
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            BigBikeProgressIndicator(
                currentDistance = currentDistance,
                totalDistance = totalDistance,
                iconSize = iconSize,
                containerHeight = buttonSize,
                trackHeight = trackHeight
            )
        }

        // Stop (Save) button
        FloatingActionButton(
            onClick = { onBikeEvent(BikeEvent.StopSaveRide) },
            containerColor = Color.White,
            contentColor = Color.Red,
            modifier = Modifier.size(buttonSize)
        ) {
            Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BikePathWithControlsPreview() {
    val demoInfo = BikeRideInfo(
        location = null,
        currentSpeed = 0.0,
        averageSpeed = 0.0,
        currentTripDistance = 5f,
        totalTripDistance = 20f,
        remainingDistance = 15f,
        rideDuration = "00:10",
        settings = emptyMap(),
        heading = 0f,
        elevation = 0.0,
        isBikeConnected = false,
        batteryLevel = null,
        motorPower = null,
        rideState = RideState.NotStarted
    )
    BikePathWithControls(
        bikeRideInfo = demoInfo,
        onBikeEvent = {},
    )
}
