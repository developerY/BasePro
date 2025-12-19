package com.ylabz.basepro.ashbike.mobile.features.glass.ui

// Glimmer Imports
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.surface
import com.ylabz.basepro.ashbike.mobile.features.glass.R

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: GlassUiState,
    onEvent: (GlassUiEvent) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .surface(focusable = false) // Glimmer Surface
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // MAIN CONTAINER CARD
        Card(
            title = { Text("AshBike Control") },
            action = {
                Button(onClick = { onEvent(GlassUiEvent.CloseApp) }) {
                    Text(stringResource(id = R.string.close))
                }
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {

                // --- NEW: SPEED CARD ---
                // We nest a smaller card here for the speedometer
                Card(
                    modifier = Modifier.padding(bottom = 12.dp),
                    // You could add an icon here if you wanted
                    // leadingIcon = { Icon(Icons.Default.Speed, "") }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Speed (mph)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = uiState.currentSpeed,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // --- EXISTING: GEAR CONTROLS ---
                Text(text = "Gear", style = MaterialTheme.typography.labelMedium)
                Text(text = "${uiState.currentGear}", fontSize = 48.sp) // Reduced size slightly to fit

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { onEvent(GlassUiEvent.GearDown) }) {
                        Text("-")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { onEvent(GlassUiEvent.GearUp) },
                        modifier = Modifier.focusRequester(focusRequester)
                    ) {
                        Text("+")
                    }
                }


                /* --- NEW SUSPENSION ROW ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Suspension:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onEvent(GlassUiEvent.ToggleSuspension)
                        }
                    ) {
                        // Shows: "Open", "Trail", or "Lock"
                        Text("Suspension Setting ... ")//state.suspension.label)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                // Secondary Control: Open the Full List
                Button(onClick = { onEvent(GlassUiEvent.OpenGearList) }) {
                    Text("Select from List")
                }
                */
            }
        }
    }

    // Force focus to the "+" button on start so trackpad works immediately
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun HomeScreenOrig(modifier: Modifier = Modifier, onClose: () -> Unit) {
    Box(
        modifier = modifier
            .surface(focusable = false)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            title = { Text(stringResource(id = R.string.app_name)) },
            action = {
                Button(onClick = {
                    onClose()
                }) {
                    Text(stringResource(id = R.string.close))
                }
            }
        ) {
            Text(stringResource(id = R.string.hello_ai_glasses))
        }
    }
}
