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
    // 1. RESTORE FOCUS REQUESTER
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .surface(focusable = false)
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 2. DYNAMIC HEADER TITLE
                    // Replaces "ASHBIKE" with "GEAR X" when connected
                    if (uiState.isBikeConnected) {
                        Text(
                            text = "GEAR ${uiState.currentGear}",
                            color = Color(0xFF4CAF50), // Green for Active
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "ASHBIKE",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    // RIGHT: STATUS COLUMN
                    Column(horizontalAlignment = Alignment.End) {
                        BikeConnectionStatus(isConnected = uiState.isBikeConnected)
                        Spacer(modifier = Modifier.height(4.dp))
                        BatteryStatusDisplay(
                            zone = uiState.batteryZone,
                            levelText = uiState.formattedBattery, // Updated to use Text property
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            },
            action = {
                Button(onClick = { onEvent(GlassUiEvent.CloseApp) }) {
                    Text("EXIT", fontSize = 12.sp)
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.weight(0.65f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    // --- LEFT CARD: SPEED (Always Visible) ---
                    Box(modifier = Modifier.weight(1f).padding(end = 6.dp, bottom = 6.dp)) {
                        Card(modifier = Modifier.fillMaxSize()) {
                            MetricDisplay(
                                label = "SPEED",
                                value = uiState.formattedSpeed, // NOW SHOWS ALWAYS
                                bottomContent = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.Explore,
                                            contentDescription = null,
                                        )
                                        Text(
                                            text = uiState.formattedHeading, // NOW SHOWS ALWAYS
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            )
                        }
                    }

                    // --- RIGHT CARD: DYNAMIC CONTENT ---
                    Box(modifier = Modifier.weight(1f).padding(start = 6.dp, bottom = 6.dp)) {
                        if (uiState.isBikeConnected) {
                            // CASE A: CONNECTED -> Show Gear Controls (For Focus!)
                            GearControlPanel(
                                currentGear = uiState.currentGear,
                                onGearUp = { onEvent(GlassUiEvent.GearUp) },
                                onGearDown = { onEvent(GlassUiEvent.GearDown) },
                                focusRequester = focusRequester // <--- ATTACHED HERE
                            )
                        } else {
                            // CASE B: DISCONNECTED -> Show Stats List
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
            }

            // 3. REQUEST FOCUS LOGIC
            // Automatically grab focus on the "Gear Up" button when the bike connects
            LaunchedEffect(uiState.isBikeConnected) {
                if (uiState.isBikeConnected) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}