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
import com.ylabz.basepro.applications.bike.ui.components.home.dials.SpeedometerWithCompassOverlay
import com.ylabz.basepro.applications.bike.ui.components.home.dials.WeatherBadge
import com.ylabz.basepro.applications.bike.ui.components.path.BigBikeProgressIndicator
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif
import com.ylabz.basepro.feature.weather.ui.components.combine.WindDirectionDialWithSpeed


@Composable
fun BikePathWithControls(
    currentDistance: Float,
    totalDistance: Float?,
    isRiding: Boolean,
    onStartPauseClicked: () -> Unit,
    onStopClicked: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 48.dp,
    trackHeight: Dp = 8.dp,
    buttonSize: Dp = 60.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(buttonSize)  // match the size of the FAB
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1) LEFT BUTTON
        Box(
            modifier = Modifier
                .size(buttonSize),
            contentAlignment = Alignment.Center
        ) {
            // You can also just put a FloatingActionButton here.
            FloatingActionButton(
                onClick = onStartPauseClicked,
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                val icon = if (isRiding) Icons.Default.Pause else Icons.Default.PlayArrow
                val desc = if (isRiding) "Pause" else "Start"
                Icon(imageVector = icon, contentDescription = desc)
            }
        }

        // 2) PROGRESS INDICATOR
        // We'll make this composable measure how wide each button is,
        // then draw a line starting from left-button center to right-button center.
        Box(
            modifier = Modifier
                .weight(1f)  // Take all the remaining horizontal space
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            // The actual line is drawn inside BigBikeProgressIndicator
            // We'll pass an offset so it knows the path starts at x=0 and ends at the full width
            BigBikeProgressIndicator(
                currentDistance = currentDistance,
                totalDistance = totalDistance,
                iconSize = iconSize,
                containerHeight = buttonSize,
                trackHeight = trackHeight,
                // Optional: You might add parameters that shift the starting or ending
                // anchor so it visually looks like it’s “inside” the buttons.
            )
        }

        // 3) RIGHT BUTTON
        Box(
            modifier = Modifier
                .size(buttonSize),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                onClick = onStopClicked,
                containerColor = Color.White,
                contentColor = Color.Red
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop"
                )
            }
        }
    }
}

@Preview
@Composable
fun BikePathWithControlsPreview() {
    BikePathWithControls(
        currentDistance = 0f,
        totalDistance = 2000f,
        isRiding = true,
        onStartPauseClicked = {},
        onStopClicked = {}
    )
}