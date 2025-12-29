package com.ylabz.basepro.ashbike.mobile.features.glass.ui.screens

// Glimmer Imports
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.surface
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassUiEvent
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassUiState
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.status.BatteryStatusDisplay
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.status.BikeConnectionStatus
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.telemetry.MetricDisplay
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.panels.RideStatScrolList

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: GlassUiState,
    onEvent: (GlassUiEvent) -> Unit
) {
    // 1. Centralized Focus Management
    val headerFocusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .surface(focusable = false)
            .fillMaxSize()
            .background(GlimmerTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            title = {
                // --- COMPOSABLE 1: HEADER ---
                HomeHeader(
                    uiState = uiState,
                    onGearUp = { onEvent(GlassUiEvent.GearUp) },
                    onGearDown = { onEvent(GlassUiEvent.GearDown) },
                    focusRequester = headerFocusRequester
                )
            },
            action = {
                Button(onClick = { onEvent(GlassUiEvent.CloseApp) }) {
                    Text("EXIT", style = GlimmerTheme.typography.labelMedium)
                }
            }
        ) {
            // --- COMPOSABLE 2: MAIN CONTENT (Vertical Stack) ---
            HomeContent(uiState = uiState)

            // Auto-focus logic stays here
            LaunchedEffect(uiState.isBikeConnected) {
                if (uiState.isBikeConnected) {
                    headerFocusRequester.requestFocus()
                }
            }
        }
    }
}