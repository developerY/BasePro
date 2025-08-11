package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable // Added import
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent // Added import
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.core.model.bike.LocationEnergyLevel
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.core.ui.NavigationCommand
// import com.ylabz.basepro.applications.bike.ui.navigation.BikeScreen // No longer needed directly by GpsLevelIndicator
import kotlinx.coroutines.launch

// WORKAROUND: Manually define the Color VectorConverter because it cannot be found
// with the current Compose BOM version.
private val ColorToVectorConverter =
    TwoWayConverter<Color, AnimationVector4D>(
        convertToVector = { color ->
            AnimationVector4D(color.red, color.green, color.blue, color.alpha)
        },
        convertFromVector = { vector ->
            Color(vector.v1, vector.v2, vector.v3, vector.v4)
        }
    )

val LowEnergyColor = Color(0xFF4CAF50) // Green
val MidEnergyColor = Color(0xFFFFC107) // Amber
val HighEnergyColor = Color(0xFFF44336) // Red

@Composable
fun GpsLevelIndicator(
    uiState: BikeUiState.Success,
    onEvent: (BikeEvent) -> Unit, // Modified signature
    navTo: (NavigationCommand) -> Unit, // MODIFIED: Changed from onEvent to navTo
    modifier: Modifier = Modifier,
) {
    val bikeData = uiState.bikeData
    val lastUpdateTime = bikeData.lastGpsUpdateTime
    val gpsUpdateInterval = bikeData.gpsUpdateIntervalMillis
    val showCountdown = uiState.showGpsCountdown
    val currentEnergyLevel = uiState.locationEnergyLevel

    val iconColor by animateColorAsState(
        targetValue = when (currentEnergyLevel) {
            LocationEnergyLevel.POWER_SAVER -> LowEnergyColor
            LocationEnergyLevel.BALANCED -> MidEnergyColor
            LocationEnergyLevel.HIGH_ACCURACY -> HighEnergyColor
            LocationEnergyLevel.AUTO -> MidEnergyColor // Defaulting AUTO to MidEnergyColor
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 500),
        label = "GPS Icon Color"
    )

    val animatedColor = remember { Animatable(iconColor, ColorToVectorConverter) }
    val initialColor = MaterialTheme.colorScheme.onSurface

    LaunchedEffect(lastUpdateTime, currentEnergyLevel) {
        if (lastUpdateTime > 0L) {
            launch {
                animatedColor.snapTo(Color.Blue)
                animatedColor.animateTo(
                    targetValue = when (currentEnergyLevel) {
                        LocationEnergyLevel.POWER_SAVER -> LowEnergyColor
                        LocationEnergyLevel.BALANCED -> MidEnergyColor
                        LocationEnergyLevel.HIGH_ACCURACY -> HighEnergyColor
                        LocationEnergyLevel.AUTO -> MidEnergyColor // Defaulting AUTO to MidEnergyColor
                        //else -> MaterialTheme.colorScheme.onSurface
                    },
                    animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                )
            }
        }
    }

    Box(
        modifier = modifier.clickable {
            // val route = "${BikeScreen.SettingsBikeScreen.route}?cardToExpandArg=AppPrefs" // <<< MODIFIED NAVIGATION CALL
            // navTo(route)
            Log.d("GpsLevelIndicator", "Satellite icon clicked. Sending NavigateToSettingsRequested event.")
            onEvent(BikeEvent.NavigateToSettingsRequested(cardKey = "AppPrefs"))
        },
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
            val icon = if (showCountdown) Icons.Default.SatelliteAlt else Icons.Default.Satellite
            Icon(
                imageVector = icon,
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
    color: Color? = null,
    strokeWidth: Dp = 3.dp
) {
    val progress = remember { Animatable(0f) }
    val countdownColor = color ?: MaterialTheme.colorScheme.primary

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
            color = countdownColor,
            startAngle = -90f, // Start from the top
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx())
        )
    }
}
