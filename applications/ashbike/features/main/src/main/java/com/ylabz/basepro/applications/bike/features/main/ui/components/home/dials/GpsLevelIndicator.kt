package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path.ColorToVectorConverter
import kotlinx.coroutines.launch

@Composable
fun GpsLevelIndicator(
    uiState: BikeUiState.Success,
    modifier: Modifier = Modifier,
) {
    val bikeData = uiState.bikeData
    val lastUpdateTime = bikeData.lastGpsUpdateTime
    val gpsUpdateInterval = bikeData.gpsUpdateIntervalMillis
    val showCountdown = uiState.showGpsCountdown

    val animatedColor = remember { Animatable(MaterialTheme.colorScheme.onSurface, ColorToVectorConverter) }

    LaunchedEffect(lastUpdateTime) {
        if (lastUpdateTime > 0L) {
            launch {
                animatedColor.animateTo(Color.Blue, animationSpec = tween(durationMillis = 250))
                animatedColor.animateTo(
                    MaterialTheme.colorScheme.onSurface,
                    animationSpec = tween(durationMillis = 500)
                )
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val indicatorContainerSize = 24.dp + 8.dp
        Box(
            modifier = Modifier
                .size(indicatorContainerSize),
            contentAlignment = Alignment.Center
        ) {
            if (showCountdown && lastUpdateTime > 0L && gpsUpdateInterval > 0) {
                GpsCountdownIndicator(
                    lastGpsUpdateTime = lastUpdateTime,
                    gpsUpdateIntervalMillis = gpsUpdateInterval,
                    modifier = Modifier.matchParentSize(),
                    color = animatedColor.value.copy(alpha = 0.8f)
                )
            }
            Icon(
                imageVector = Icons.Default.SatelliteAlt,
                contentDescription = "GPS Status",
                tint = animatedColor.value,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun GpsCountdownIndicator(
    lastGpsUpdateTime: Long,
    gpsUpdateIntervalMillis: Long,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 3.dp
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(lastGpsUpdateTime) {
        if (gpsUpdateIntervalMillis > 0L) {
            progress.snapTo(1f) // Start from a full circle
            progress.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = gpsUpdateIntervalMillis.toInt(),
                    easing = LinearEasing
                )
            )
        }
    }

    val sweepAngle = 360 * progress.value

    Canvas(modifier) {
        drawArc(
            color = color,
            startAngle = -90f, // Start from the top
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx())
        )
    }
}