package com.ylabz.basepro.ashbike.mobile.features.glass.newui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.surface
import com.ylabz.basepro.ashbike.mobile.features.glass.newui.sections.HeaderBar
import com.ylabz.basepro.ashbike.mobile.features.glass.newui.sections.StatsBoard
import com.ylabz.basepro.ashbike.mobile.features.glass.newui.sections.VelocityDash
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassUiEvent
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassUiState
import kotlinx.coroutines.delay

@Composable
fun AshGlassLayout(
    modifier: Modifier = Modifier,
    uiState: GlassUiState,
    onEvent: (GlassUiEvent) -> Unit
) {
    // New Focus Requester for the Gear controls
    // val gearFocus = remember { FocusRequester() }
    val statsFocus = remember { FocusRequester() } // 1. New Focus Requester for List

    Box(
        modifier = modifier
            .surface(focusable = false)
            .fillMaxSize()
            .background(GlimmerTheme.colors.surface)
            .onFocusChanged {
                if (it.isFocused) Log.d("FOCUS_DEBUG", "Speed Card got Focus!")
            },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(8.dp).fillMaxSize(),
            title = {
                // 1. TOP HEADER (Replaced 'HomeHeader')
                HeaderBar(
                    isConnected = uiState.isBikeConnected,
                    gear = uiState.currentGear,
                    batteryZone = uiState.batteryZone,
                    batteryText = uiState.formattedBattery,
                    onGearUp = { onEvent(GlassUiEvent.GearUp) },
                    onGearDown = { onEvent(GlassUiEvent.GearDown) },
                    // focusRequester = gearFocus
                )
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
            // wrapContentHeight() ensures it only takes the space it needs.
            // FIXED COLUMN LAYOUT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {


                // 1. FIXED TOP: VELOCITY DASH
                // ITEM A: The Big Speedometer
                // 1. FIXED TOP: VELOCITY DASH
                // wrapContentHeight() ensures it only takes the space it needs.
                // 1. VELOCITY DASH (Non-focusable display)
                Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                    VelocityDash(
                        speed = uiState.formattedSpeed,
                        heading = uiState.formattedHeading
                    )
                }


                // ITEM B: The Detailed Stats
                // 2. SCROLLABLE BOTTOM: STATS BOARD
                // weight(1f) tells it to fill all remaining vertical space.
                // The scrolling happens INSIDE StatsBoard now.
                // 2. STATS BOARD (Focus Target)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        // 2. Apply Focus logic here so D-Pad/Scroll works immediately
                        .focusRequester(statsFocus)
                        .focusable()
                ) {
                    StatsBoard(
                        distance = uiState.tripDistance,
                        duration = uiState.rideDuration,
                        avgSpeed = uiState.averageSpeed,
                        calories = uiState.calories,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // 1. The Trigger
    LaunchedEffect(Unit) {
        Log.d("FOCUS_DEBUG", "Requesting focus now...")
        delay(300) // Increase to 300ms or 500ms temporarily to test

        try {
            statsFocus.requestFocus()
            Log.d("FOCUS_DEBUG", "Request sent.")
        } catch (e: Exception) {
            Log.e("FOCUS_DEBUG", "Focus request failed: ${e.message}")
        }
    }

    // 3. Request Focus on the Stats List on launch
    /* LaunchedEffect(Unit) {
        delay(200) // Wait 200ms for UI to build
        // We delay slightly to ensure layout is ready, or just call it directly
        statsFocus.requestFocus()
    } */

    /* Auto-focus logic
    LaunchedEffect(uiState.isBikeConnected) {
        if (uiState.isBikeConnected) gearFocus.requestFocus()
    }*/
}