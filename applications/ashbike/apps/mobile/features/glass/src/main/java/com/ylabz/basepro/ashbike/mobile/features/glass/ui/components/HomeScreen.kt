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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
    // 1. Focus Requester for the Header Controls
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
                    // =========================================================
                    // HEADER LOGIC: Title OR Controls
                    // =========================================================
                    if (uiState.isBikeConnected) {
                        // CASE A: CONNECTED -> Show Gear Controls IN HEADER
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 1. The Label
                            Text(
                                text = "GEAR ${uiState.currentGear}",
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )

                            Spacer(Modifier.width(16.dp))

                            // 2. Down Button (-)
                            Button(
                                onClick = { onEvent(GlassUiEvent.GearDown) },
                                modifier = Modifier.size(40.dp),
                                /* colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    containerColor = Color.DarkGray
                                ) */
                            ) {
                                Icon(androidx.compose.material.icons.Icons.Default.Remove, contentDescription = "Down")
                            }

                            Spacer(Modifier.width(8.dp))

                            // 3. Up Button (+) -> REQUESTS FOCUS
                            Button(
                                onClick = { onEvent(GlassUiEvent.GearUp) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .focusRequester(focusRequester), // <--- FOCUS HERE
                                /*colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.Black,
                                    containerColor = Color.White
                                )*/
                            ) {
                                Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Up")
                            }
                        }
                    } else {
                        // CASE B: DISCONNECTED -> Show App Title
                        Text(
                            text = "ASHBIKE",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    // RIGHT: STATUS COLUMN (Always visible)
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
            },
            action = {
                // Exit Button
                Button(onClick = { onEvent(GlassUiEvent.CloseApp) }) {
                    Text("EXIT", fontSize = 12.sp)
                }
            }
        ) {
            // =========================================================
            // MAIN CONTENT (Always Speed + Stats)
            // =========================================================
            Column(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.weight(0.65f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    // LEFT CARD: SPEED (Always Visible)
                    Box(modifier = Modifier.weight(1f).padding(end = 6.dp, bottom = 6.dp)) {
                        Card(modifier = Modifier.fillMaxSize()) {
                            MetricDisplay(
                                label = "SPEED",
                                value = uiState.formattedSpeed,
                                bottomContent = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.Explore,
                                            contentDescription = null,
                                        )
                                        Text(
                                            text = uiState.formattedHeading,
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            )
                        }
                    }

                    // RIGHT CARD: STATS (NOW PERMANENT!)
                    // No more if/else switching. Stats are always here.
                    Box(modifier = Modifier.weight(1f).padding(start = 6.dp, bottom = 6.dp)) {
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

            // 3. Auto-Focus Logic
            LaunchedEffect(uiState.isBikeConnected) {
                if (uiState.isBikeConnected) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}