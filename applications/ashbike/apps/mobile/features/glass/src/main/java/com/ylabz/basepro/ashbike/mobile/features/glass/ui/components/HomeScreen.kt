package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components

// Glimmer Imports
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // You could add an App Icon here
                    Text("ASHBIKE", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    // Status Pill (e.g. Battery or Connection)
                    Text("● CONNECTED", color = GlassColors.NeonGreen, fontSize = 10.sp)
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

                // --- LEFT: TELEMETRY (Speed & Heading) ---
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // We can use a visual container for the speed
                    Card(modifier = Modifier.fillMaxWidth()) {
                        MetricDisplay(
                            label = "SPEED (MPH)",
                            value = uiState.currentSpeed,
                            subValue = uiState.heading // e.g. "350° N"
                        )
                    }
                }

                // --- RIGHT: CONTROLS (Gear Shifting) ---
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    GearControlPanel(
                        currentGear = uiState.currentGear,
                        onGearUp = { onEvent(GlassUiEvent.GearUp) },
                        onGearDown = { onEvent(GlassUiEvent.GearDown) },
                        focusRequester = focusRequester
                    )
                }
            }
        }
    }
}
