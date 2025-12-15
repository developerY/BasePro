package com.ylabz.basepro.ashbike.mobile.features.glass.ui

// Glimmer Imports
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.surface
import com.ylabz.basepro.ashbike.mobile.features.glass.R
import com.ylabz.basepro.ashbike.mobile.features.glass.data.GlassBikeRepository

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    currentGear: Int,
    onGearChange: (Int) -> Unit,
    onOpenGearList: () -> Unit,
    onClose: () -> Unit,
    repository: GlassBikeRepository,
) {

    // We use this to force focus onto the "+" button when the screen loads
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .surface(focusable = false)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            title = { Text("AshBike Control") },
            action = {
                Button(onClick = onClose) {
                    Text(stringResource(id = R.string.close))
                }
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Current Gear")

                // Big text for visibility
                Text(text = "Current Gear", fontSize = 24.sp)
                Text(text = "$currentGear", fontSize = 60.sp) // Shows the synced value

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Controls
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // DOWN BUTTON
                    Button(
                        onClick = {
                            onGearChange(currentGear - 1)
                            repository.gearDown()
                        }
                    ) {
                        Text("-")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // UP BUTTON (Default Focus)
                    Button(
                        onClick = {
                            onGearChange(currentGear + 1)
                            repository.gearUp()
                        },
                        modifier = Modifier.focusRequester(focusRequester)
                    ) {
                        Text("+")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Secondary Control: Open the Full List
                Button(onClick = onOpenGearList) {
                    Text("Select from List")
                }
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
