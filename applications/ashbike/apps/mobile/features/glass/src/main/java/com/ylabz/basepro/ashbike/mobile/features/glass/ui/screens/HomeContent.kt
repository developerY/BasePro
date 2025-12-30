package com.ylabz.basepro.ashbike.mobile.features.glass.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassUiState
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.panels.RideStatsPanel
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.panels.SpeedPanel

@Composable
fun HomeContent(uiState: GlassUiState) {
    // 1. MAIN SCROLLING CONTAINER
    // This allows the Speed Card to grow as large as needed without clipping.
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- SECTION 1: SPEED PANEL (40% Height) ---
        item {
            SpeedPanel(
                speed = uiState.formattedSpeed,
                heading = uiState.formattedHeading
            )
        }

        // --- SECTION 2: STATS LIST (60% Height) ---
        item {
            RideStatsPanel(
                distance = uiState.tripDistance,
                duration = uiState.rideDuration,
                avgSpeed = uiState.averageSpeed,
                calories = uiState.calories,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}