package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components

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
import androidx.compose.material.icons.filled.Explore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Icon
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
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .surface(focusable = false)
            .fillMaxSize()
            .background(Color.Black), // Pure black is transparent on AR
        contentAlignment = Alignment.Center
    ) {
        // MAIN HUD CONTAINER
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(), // Fill the available HUD space
            title = {
                // HEADER ROW
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // LEFT: APP TITLE
                    Text("ASHBIKE", color = Color.White, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.weight(1f)) // Pushes status to the right

                    // RIGHT: STATUS COLUMN (Connected + Battery)
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        // Status Text
                        // 1. BIKE CONNECTION (New Component)
                        BikeConnectionStatus(isConnected = uiState.isBikeConnected)

                        // Vertical Space (Fixed: changed width to height)
                        Spacer(modifier = Modifier.height(4.dp))

                        // Battery Component
                        BatteryStatusDisplay(
                            level = uiState.batteryLevel
                        )
                    }
                }
            },
            action = {
                // Subtle Close Button
                Button(onClick = { onEvent(GlassUiEvent.CloseApp) }) {
                    Text("EXIT", fontSize = 12.sp)
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                // LAYOUT: Side-by-Side (Left: Speed, Right: Gear) ]
                // --- ROW 1: TOP SECTION ---
                Row(
                    modifier = Modifier.weight(0.65f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    // --- LEFT: TELEMETRY ---
                    // 1. LEFT: SPEED CARD (Always Visible)
                    // LEFT: SPEED (Always Visible)
                    Box(modifier = Modifier.weight(1f).padding(end = 6.dp, bottom = 6.dp)) {
                        Card(modifier = Modifier.fillMaxSize()) {
                            MetricDisplay(
                                label = "SPEED",
                                value = uiState.currentSpeed,
                                bottomContent = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.Explore,
                                            contentDescription = null,
                                        )
                                        Text(
                                            text = uiState.heading,
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            )
                        }
                    }

                    // --- RIGHT: CONTROLS (Gear Shifting) ---
                    // 2. RIGHT: DYNAMIC PANEL (Gears OR Stats)
                    // RIGHT: DYNAMIC PANEL (Gears OR Stats)
                    Box(modifier = Modifier.weight(1f).padding(start = 6.dp, bottom = 6.dp)) {
                        if (uiState.isBikeConnected) {
                            // OPTION A: Connected -> Show Gears
                            GearControlPanel(
                                currentGear = uiState.currentGear,
                                onGearUp = { onEvent(GlassUiEvent.GearUp) },
                                onGearDown = { onEvent(GlassUiEvent.GearDown) },
                                focusRequester = focusRequester // Only focus if visible
                            )
                        } else {
                            // OPTION B: Disconnected -> Show Stats
                            RideStatsDisplayPanel(
                                distance = uiState.tripDistance,
                                calories = uiState.calories,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
            // 2. FORCE FOCUS ON LAUNCH
            // This tells the system: "Ignore everything else, look at the Plus button."
            // only if the bike BLE is connected
            // Only request focus if the bike is connected (and thus the Gear buttons exist)
            LaunchedEffect(uiState.isBikeConnected) {
                if (uiState.isBikeConnected) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}
