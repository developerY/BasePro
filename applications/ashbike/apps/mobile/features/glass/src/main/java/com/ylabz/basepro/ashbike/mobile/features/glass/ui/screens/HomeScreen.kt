package com.ylabz.basepro.ashbike.mobile.features.glass.ui.screens

// Glimmer Imports
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
                    Text("EXIT", style = GlimmerTheme.typography.bodyMedium)
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