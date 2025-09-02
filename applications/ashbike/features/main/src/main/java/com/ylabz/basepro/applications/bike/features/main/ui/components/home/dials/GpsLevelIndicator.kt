package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

//import androidx.compose.ui.tooling.preview.Preview
// import com.ylabz.basepro.applications.bike.ui.navigation.BikeScreen // No longer needed directly by GpsLevelIndicator
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Satellite
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.core.model.bike.LocationEnergyLevel
import com.ylabz.basepro.core.ui.NavigationCommand
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

private val DefaultCountdownStrokeWidth = 3.dp

@Composable
fun GpsLevelIndicator(
    modifier: Modifier = Modifier,
    uiState: BikeUiState.Success,
    onEvent: (BikeEvent) -> Unit, // Modified signature
    navTo: (NavigationCommand) -> Unit, // MODIFIED: Changed from onEvent to navTomodifier: Modifier = Modifier
) {

    GpsLevelIndicatorFull(
        modifier = modifier,
        uiState = uiState,
        onEvent = onEvent,
        navTo = navTo
    )

    /*val icon = Icons.Default.SatelliteAlt //  Icons.Default.Satellite
    Icon(
        imageVector = icon,
        contentDescription = "GPS Status",
        // tint = animatedColor.value,
        modifier = Modifier.size(24.dp)
    )*/
}


@Composable
fun GpsLevelIndicatorFull(
    modifier: Modifier = Modifier,
    uiState: BikeUiState.Success,
    onEvent: (BikeEvent) -> Unit, // Modified signature
    navTo: (NavigationCommand) -> Unit, // MODIFIED: Changed from onEvent to navTo
) {
    val bikeData = uiState.bikeData
    val lastUpdateTime = bikeData.lastGpsUpdateTime
    val gpsUpdateInterval = bikeData.gpsUpdateIntervalMillis
    val showCountdown = uiState.showGpsCountdown
    val currentEnergyLevel = uiState.locationEnergyLevel

    // Determine initial color based on currentEnergyLevel
    val initialAnimatedColor = when (currentEnergyLevel) {
        LocationEnergyLevel.POWER_SAVER -> LowEnergyColor
        LocationEnergyLevel.BALANCED -> MidEnergyColor
        LocationEnergyLevel.HIGH_ACCURACY -> HighEnergyColor
        LocationEnergyLevel.AUTO -> MidEnergyColor // Defaulting AUTO to MidEnergyColor
        else -> MaterialTheme.colorScheme.onSurface // Fallback for any other state
    }
    val animatedColor = remember { Animatable(initialAnimatedColor, ColorToVectorConverter) }

    LaunchedEffect(lastUpdateTime, currentEnergyLevel) {
        val targetColor = when (currentEnergyLevel) {
            LocationEnergyLevel.POWER_SAVER -> LowEnergyColor
            LocationEnergyLevel.BALANCED -> MidEnergyColor
            LocationEnergyLevel.HIGH_ACCURACY -> HighEnergyColor
            LocationEnergyLevel.AUTO -> MidEnergyColor // Defaulting AUTO to MidEnergyColor
            //else -> MaterialTheme.colorScheme.onSurface
        }

        if (lastUpdateTime > 0L) {
            launch {
                // Only snap to blue if the color is actually changing,
                // or if it's a new GPS update triggering the blue flash.
                // This avoids snapping if only currentEnergyLevel changed but GPS wasn't just called.
                // However, the current logic keys on lastUpdateTime, so this launch block
                // will run on each new lastUpdateTime if > 0.
                animatedColor.snapTo(Color.Blue)
                animatedColor.animateTo(
                    targetValue = targetColor,
                    animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                )
            }
        } else {
            // If GPS is not active (lastUpdateTime <= 0L),
            // still ensure the icon color reflects the currentEnergyLevel, but without the blue flash.
            launch {
                if (animatedColor.value != targetColor) {
                    animatedColor.animateTo(
                        targetValue = targetColor,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = LinearEasing
                        ) // Faster animation if no blue flash
                    )
                }
            }
        }
    }

    Box(
        modifier = modifier.clickable {
            Log.d(
                "GpsLevelIndicator",
                "Satellite icon clicked. Sending NavigateToSettingsRequested event."
            )
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
    color: Color? = null
) {
    val progress = remember { Animatable(0f) }
    val countdownColor = color ?: MaterialTheme.colorScheme.primary

    // Create the Stroke style. Since DefaultCountdownStrokeWidth is a constant Dp,
    // and Density is locally constant within a composition, this effectively remembers
    // the Stroke style unless the density changes.
    val strokeWidthInPx = LocalDensity.current.run { DefaultCountdownStrokeWidth.toPx() }
    val countdownStrokeStyle = remember(strokeWidthInPx) {
        Stroke(width = strokeWidthInPx)
    }

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
            style = countdownStrokeStyle // Use the remembered Stroke style
        )
    }
}
/*
@Preview
@Composable
fun GpsLevelIndicatorPreview() {
    val bikeData = BikeRideInfo(
        location = null,
        currentSpeed = 0.0,
        averageSpeed = 0.0,
        maxSpeed = 0.0,
        currentTripDistance = 0f,
        totalTripDistance = null,
        remainingDistance = null,
        elevationGain = 0.0,
        elevationLoss = 0.0,
        caloriesBurned = 0,
        rideDuration = "0h 0m",
        settings = persistentMapOf(),
        heading = 0f,
        elevation = 0.0,
        isBikeConnected = false,
        heartbeat = null,
        batteryLevel = null,
        motorPower = null,
        lastGpsUpdateTime = System.currentTimeMillis(),
        gpsUpdateIntervalMillis = 5000L
    )
    val uiState = BikeUiState.Success(
        bikeData = bikeData,
        showGpsCountdown = true,
        locationEnergyLevel = LocationEnergyLevel.BALANCED
    )
    GpsLevelIndicator(
        uiState = uiState,
        onEvent = {},
        navTo = {}
    )
}

@Preview
@Composable
fun GpsCountdownIndicatorPreview() {
    GpsCountdownIndicator(
        lastGpsUpdateTime = System.currentTimeMillis(),
        gpsUpdateIntervalMillis = 5000L,
        modifier = Modifier.size(100.dp),
        color = Color.Green
    )
}

*/