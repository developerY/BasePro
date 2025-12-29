package com.ylabz.basepro.ashbike.mobile.features.glass.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassUiState
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.status.BatteryStatusDisplay
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.status.BikeConnectionStatus

@Composable
fun HomeHeader(
    uiState: GlassUiState,
    onGearUp: () -> Unit,
    onGearDown: () -> Unit,
    focusRequester: FocusRequester
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (uiState.isBikeConnected) {
            // --- STATE A: CONNECTED (Gear Controls) ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "GEAR ${uiState.currentGear}",
                    color = GlimmerTheme.colors.positive, // Allowed: Green
                    style = GlimmerTheme.typography.titleLarge
                )
                Spacer(Modifier.width(16.dp))
                Button(onClick = onGearDown, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Down")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onGearUp,
                    modifier = Modifier.size(40.dp).focusRequester(focusRequester)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Up")
                }
            }
        } else {
            // --- STATE B: DISCONNECTED (Brand Title) ---
            Text(
                text = "ASHBIKE",
                color = GlimmerTheme.colors.primary,
                style = GlimmerTheme.typography.titleLarge
            )
        }

        Spacer(Modifier.weight(1f))

        // --- STATUS COLUMN ---
        Column(horizontalAlignment = Alignment.End) {
            BikeConnectionStatus(isConnected = uiState.isBikeConnected)
            Spacer(modifier = Modifier.height(4.dp))
            BatteryStatusDisplay(
                zone = uiState.batteryZone,
                levelText = uiState.formattedBattery,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}