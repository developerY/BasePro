package com.ylabz.basepro.ashbike.mobile.features.glass.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassUiState
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.telemetry.MetricDisplay
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.panels.RideStatScrolList

@Composable
fun HomeContent(uiState: GlassUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- SECTION 1: SPEED PANEL (40% Height) ---
        Box(modifier = Modifier.weight(0.4f).fillMaxWidth()) {
            SpeedPanel(
                speed = uiState.formattedSpeed,
                heading = uiState.formattedHeading
            )
        }

        // --- SECTION 2: STATS LIST (60% Height) ---
        Box(modifier = Modifier.weight(0.6f).fillMaxWidth()) {
            RideStatScrolList(
                distance = uiState.tripDistance,
                duration = uiState.rideDuration,
                avgSpeed = uiState.averageSpeed,
                calories = uiState.calories,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun SpeedPanel(speed: String, heading: String) {
    Card(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speed Value
            MetricDisplay(
                label = "SPEED",
                value = speed,
                highlightColor = GlimmerTheme.colors.primary,
                bottomContent = {}
            )

            // Heading with Icon
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.Explore,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    // Use Secondary (Darker Blue) for Icons to distinguish from Text
                    tint = GlimmerTheme.colors.secondary
                )
                Text(
                    text = heading,
                    style = GlimmerTheme.typography.titleMedium // Strict: Second-Largest Title
                    // No Color set -> Defaults to Calculated White
                )
            }
        }
    }
}