package com.ylabz.basepro.applications.bike.features.main.ui.components.home.main

import androidx.compose.animation.AnimatedVisibility 
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
// Removed slideInVertically and slideOutVertically as they are no longer used here
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember // Keep for weatherIconsVisible
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.GpsLevelIndicator
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.RideMap
// SlidableGoogleMap is no longer directly used here
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.SpeedometerWithCompassOverlay
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path.BikePathWithControls
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.weather.WeatherBadgeWithDetails
import com.ylabz.basepro.core.ui.NavigationCommand
import com.ylabz.basepro.feature.weather.ui.components.combine.WindDirectionDialWithSpeed

@Composable
fun SpeedAndProgressCard(
    modifier: Modifier = Modifier.fillMaxSize(),
    uiState: BikeUiState.Success, 
    onBikeEvent: (BikeEvent) -> Unit,
    navTo: (NavigationCommand) -> Unit, 
    containerColor: Color,
    contentColor: Color,
    onShowMapPanel: () -> Unit // New parameter to trigger map panel from parent
) {
    var weatherIconsVisible by remember { mutableStateOf(false) }
    // isMapPanelVisible state is removed, now managed by parent

    val bikeData = uiState.bikeData
    val currentSpeed = bikeData.currentSpeed
    val heading = bikeData.heading
    val weather = bikeData.bikeWeatherInfo

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        val cardScope = this // 'this' is ColumnScope from Card

        Box(
            modifier = Modifier.fillMaxSize() // 'this' is BoxScope here
        ) {
            SpeedometerWithCompassOverlay(
                currentSpeed = currentSpeed.toFloat(),
                maxSpeed = 60f, 
                heading = heading,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentColor = contentColor
            )

            RideMap(
                uiState = uiState,
                onEvent = onBikeEvent, 
                navTo = navTo, 
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 8.dp, start = 16.dp), 
                onMapIconClick = onShowMapPanel // Use the passed-in callback
            )

            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Toggle Weather",
                tint = contentColor.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .size(24.dp)
                    .clickable { weatherIconsVisible = !weatherIconsVisible }
            )

            GpsLevelIndicator(
                uiState = uiState,
                onEvent = onBikeEvent, 
                navTo = navTo, 
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 16.dp)
            )

            cardScope.AnimatedVisibility(
                visible = weatherIconsVisible,
                modifier = Modifier.align(Alignment.TopStart), 
                enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -it }),
                exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it })
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 56.dp, top = 8.dp) 
                        .size(48.dp)
                ) {
                    weather?.let {
                        WindDirectionDialWithSpeed(degree = it.windDegree, speed = it.windSpeed)
                    }
                }
            }

            cardScope.AnimatedVisibility(
                visible = weatherIconsVisible,
                modifier = Modifier.align(Alignment.TopEnd), 
                enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it }),
                exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { it })
            ) {
                Box(modifier = Modifier.padding(end=56.dp, top=8.dp)) { 
                    weather?.let {
                        WeatherBadgeWithDetails(weatherInfo = it)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter) 
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                BikePathWithControls(
                    uiState = uiState, 
                    onBikeEvent = onBikeEvent,
                )
            }

            // The AnimatedVisibility block for SlidableGoogleMap has been removed from here.
            // It is now managed by BikeDashboardContent.kt
        }
    }
}
