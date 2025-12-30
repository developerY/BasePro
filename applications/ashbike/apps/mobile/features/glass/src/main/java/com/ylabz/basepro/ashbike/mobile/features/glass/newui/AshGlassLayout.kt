package com.ylabz.basepro.ashbike.mobile.features.glass.newui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.surface
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassUiEvent
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassUiState
//import com.ylabz.basepro.ashbike.mobile.features.glass.newui.sections.HeaderBar
//import com.ylabz.basepro.ashbike.mobile.features.glass.newui.sections.StatsBoard
//import com.ylabz.basepro.ashbike.mobile.features.glass.newui.sections.VelocityDash

@Composable
fun AshGlassLayout(
    modifier: Modifier = Modifier,
    uiState: GlassUiState,
    onEvent: (GlassUiEvent) -> Unit
) {
    // New Focus Requester for the Gear controls
    val gearFocus = remember { FocusRequester() }

    Box(
        modifier = modifier
            .surface(focusable = false)
            .fillMaxSize()
            .background(GlimmerTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(8.dp).fillMaxSize(),
            title = {
                // 1. TOP HEADER (Replaced 'HomeHeader')
                /*HeaderBar(
                    isConnected = uiState.isBikeConnected,
                    gear = uiState.currentGear,
                    batteryZone = uiState.batteryZone,
                    batteryText = uiState.formattedBattery,
                    onGearUp = { onEvent(GlassUiEvent.GearUp) },
                    onGearDown = { onEvent(GlassUiEvent.GearDown) },
                    focusRequester = gearFocus
                )*/
            },
            action = {
                Button(onClick = { onEvent(GlassUiEvent.CloseApp) }) {
                    Text("EXIT", style = GlimmerTheme.typography.bodyMedium)
                }
            }
        ) {
            // 2. INFINITE SCROLL DASHBOARD
            // Speed is Item 0, Stats are Item 1.
            // Allows the speed card to push up when you want to see stats.
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ITEM A: The Big Speedometer
                item {
                    /*VelocityDash(
                        speed = uiState.formattedSpeed,
                        heading = uiState.formattedHeading
                    )*/
                }

                // ITEM B: The Detailed Stats
                item {
                    /*StatsBoard(
                        distance = uiState.tripDistance,
                        duration = uiState.rideDuration,
                        avgSpeed = uiState.averageSpeed,
                        calories = uiState.calories,
                        modifier = Modifier.fillMaxWidth()
                    )*/
                }
            }
        }
    }

    // Auto-focus logic
    LaunchedEffect(uiState.isBikeConnected) {
        if (uiState.isBikeConnected) gearFocus.requestFocus()
    }
}