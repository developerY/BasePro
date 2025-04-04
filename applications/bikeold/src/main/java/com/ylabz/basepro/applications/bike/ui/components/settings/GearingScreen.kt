package com.ylabz.basepro.applications.bike.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun GearingScreen(
    modifier: Modifier = Modifier
) {
    // UI state
    var autoShiftingEnabled by remember { mutableStateOf(true) }
    var targetCadence by remember { mutableStateOf(85f) }

    // Optional gradient background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFA8E6CF),  // top color
                        Color(0xFFD7FFD9)   // bottom color
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Gearing Icon",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Auto-shifting toggle
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Enable Auto-Shifting", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = autoShiftingEnabled,
                    onCheckedChange = { autoShiftingEnabled = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Target Cadence: ${targetCadence.toInt()} rpm",
                style = MaterialTheme.typography.bodyLarge
            )

            Slider(
                value = targetCadence,
                onValueChange = { targetCadence = it },
                valueRange = 50f..120f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { /* Handle gear settings */ }) {
                Text("Apply Gear Settings")
            }
        }
    }
}

// Preview
@Preview
@Composable
fun GearingScreenPreview() {
    GearingScreen()
}

