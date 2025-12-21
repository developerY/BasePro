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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
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
    // 1. Focus Requester: Controls the Gear "+" button for physical button/trackpad input
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
                // HEADER ROW: Title (Left) and Status (Right)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // LEFT: APP TITLE
                    Text("ASHBIKE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)


                    Spacer(Modifier.weight(1f)) // Pushes status to the right

                    // STATUS COLUMN: Bike Link & Battery
                    // RIGHT: STATUS COLUMN (Connected + Battery)
                    Column(horizontalAlignment = Alignment.End) {
                        // Status Text
                        // 1. BIKE CONNECTION (New Component)
                        BikeConnectionStatus(isConnected = uiState.isBikeConnected)
                        // Vertical Space (Fixed: changed width to height)
                        Spacer(modifier = Modifier.height(4.dp))
                        // Battery Component
                        BatteryStatusDisplay(level = uiState.batteryLevel)
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
            // --- QUAD GRID LAYOUT ---
            Column(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {

                // --- TOP ROW: SPEED (Left) & GEAR (Right) ---
                // ROW 1: PRIMARY METRICS (Speed & Gear) - 65% Height
                Row(
                    modifier = Modifier.weight(0.65f).fillMaxWidth(), // Takes 65% height
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Speed Card
                    Box(modifier = Modifier.weight(1f).padding(4.dp)) {
                        Card(modifier = Modifier.fillMaxSize()) {
                            // FIX IS HERE: Use the bottomContent slot
                            MetricDisplay(
                                label = "SPEED",
                                value = uiState.currentSpeed,
                                bottomContent = {
                                    // Put the Heading here inside the slot
                                    Text(
                                        text = uiState.heading, // e.g. "350° N"
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            )
                        }
                    }

                    // 2. GEAR CONTROL (Top Right)
                    Box(modifier = Modifier.weight(1f).padding(4.dp)) {
                        GearControlPanel(
                            currentGear = uiState.currentGear,
                            onGearUp = { onEvent(GlassUiEvent.GearUp) },
                            onGearDown = { onEvent(GlassUiEvent.GearDown) },
                            focusRequester = focusRequester
                        )
                    }
                }

                // --- BOTTOM ROW: POWER (Left) & HEART RATE (Right) ---
                // ROW 2: SECONDARY METRICS (Power & HR) - 35% Height
                Row(
                    modifier = Modifier.weight(0.35f).fillMaxWidth(), // Takes 35% height
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    // 3. POWER CARD (Bottom Left)
                    Box(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                        Card(modifier = Modifier.fillMaxSize()) {
                            // Simple Power Layout using Glimmer Icon
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "POWER",
                                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                    color = GlassColors.TextSecondary,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "Watts",
                                        tint = Color(0xFFFFD600), // Gold/Amber
                                        modifier = Modifier.width(20.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "${uiState.motorPower} W",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // 4. HEART RATE CARD (Bottom Right)
                    Box(modifier = Modifier.weight(1f).padding(start = 6.dp)) {
                        HeartRateCard(
                            heartRate = uiState.heartRate,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
        // 2. FORCE FOCUS ON LAUNCH
        // This tells the system: "Ignore everything else, look at the Plus button."
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}
