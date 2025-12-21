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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
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
            // LAYOUT: Side-by-Side (Left: Speed, Right: Gear)
            Row(
                modifier = Modifier.fillMaxSize().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // --- LEFT: TELEMETRY ---
                // 1. LEFT: SPEED CARD (Always Visible)
                Box(
                    modifier = Modifier.weight(1f).padding(end = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        MetricDisplay(
                            label = "SPEED (MPH)",
                            value = uiState.currentSpeed,
                            // HERE IS THE NEW "DATA ROW"
                            bottomContent = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp) // Space between items
                                ) {

                                    // 1. COMPASS
                                    DataPill(
                                        icon = Icons.Default.Explore,
                                        text = uiState.heading,
                                        color = Color.White
                                    )

                                    // 2. POWER (Watts) - Yellow
                                    DataPill(
                                        icon = Icons.Default.Bolt,
                                        text = "${uiState.motorPower} W",
                                        color = Color(0xFFFFD600) // Amber/Gold
                                    )

                                    // 3. HEART RATE - Red (Optional, if you want it)
                                    /* DataPill(
                                        icon = Icons.Default.Favorite,
                                        text = uiState.heartRate,
                                        color = GlassColors.WarningRed
                                    )*/
                                }
                            }
                        )
                    }
                }

                // --- RIGHT: CONTROLS (Gear Shifting) ---
                // 2. RIGHT: DYNAMIC PANEL (Gears OR Stats)
                Box(modifier = Modifier.weight(1f).padding(start = 6.dp, bottom = 6.dp)) {

                    if (uiState.isBikeConnected) {
                        // OPTION A: BIKE CONNECTED -> SHOW GEARS
                        GearControlPanel(
                            currentGear = uiState.currentGear,
                            onGearUp = { onEvent(GlassUiEvent.GearUp) },
                            onGearDown = { onEvent(GlassUiEvent.GearDown) },
                            focusRequester = focusRequester
                        )
                    } else {
                        // OPTION B: DISCONNECTED -> SHOW STATS
                        RideStatsPanel(
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
        LaunchedEffect(uiState.isBikeConnected) {
            if (uiState.isBikeConnected) {
                focusRequester.requestFocus()
            }
        }
    }
}
