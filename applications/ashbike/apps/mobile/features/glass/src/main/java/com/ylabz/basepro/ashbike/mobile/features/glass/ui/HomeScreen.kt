package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
// Keep your existing Glimmer imports
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.surface
import com.ylabz.basepro.ashbike.mobile.features.glass.R

@Composable
fun HomeScreen(modifier: Modifier = Modifier, onClose: () -> Unit) {
    // 1. Local UI State for the Gear
    // We use 'remember' so the glasses remember the gear as you click
    var currentGear by remember { mutableIntStateOf(1) }
    val maxGear = 12

    Box(
        modifier = modifier
            .surface(focusable = false)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            // Updated title
            title = { Text("Ride Control") },
            action = {
                Button(onClick = onClose) {
                    Text(stringResource(id = R.string.close))
                }
            }
        ) {
            // 2. Vertical Stack (Gear Number on top of Buttons)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Current Gear")

                // Display the gear number nicely
                // You might want to style this to be larger eventually
                Text(text = "$currentGear")

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Horizontal Row for the Buttons
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // GEAR DOWN
                    Button(
                        onClick = {
                            if (currentGear > 1) currentGear--
                        }
                    ) {
                        Text("-")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // GEAR UP
                    Button(
                        onClick = {
                            if (currentGear < maxGear) currentGear++
                        }
                    ) {
                        Text("+")
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenOrig(modifier: Modifier = Modifier, onClose: () -> Unit) {
    Box(
        modifier = modifier
            .surface(focusable = false).fillMaxSize(),
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
