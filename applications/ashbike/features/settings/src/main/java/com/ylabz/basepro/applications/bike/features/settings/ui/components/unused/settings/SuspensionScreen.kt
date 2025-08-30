package com.ylabz.basepro.applications.bike.features.settings.ui.components.unused.settings

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
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
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SuspensionScreen() {
    // UI state
    var riderWeight by remember { mutableStateOf(90f) }
    var suspensionMode by remember { mutableStateOf("Comfort") }
    val modes = listOf("Comfort", "Sport", "Off-Road")

    // Optional gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF7BC6CC),  // top color
                        Color(0xFFBEF2F2)   // bottom color
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
                imageVector = Icons.Default.DirectionsBike,
                contentDescription = "Bike Icon",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Rider Weight: ${riderWeight.toInt()} kg",
                style = MaterialTheme.typography.bodyLarge
            )

            Slider(
                value = riderWeight,
                onValueChange = { riderWeight = it },
                valueRange = 40f..150f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Suspension mode selection (Comfort / Sport / Off-Road)
            Row(horizontalArrangement = Arrangement.Center) {
                modes.forEach { mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { suspensionMode = mode }
                    ) {
                        RadioButton(
                            selected = (suspensionMode == mode),
                            onClick = { suspensionMode = mode }
                        )
                        Text(text = mode, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { /* Handle apply settings */ }) {
                Text("Apply Settings")
            }
        }
    }
}

/*Preview
@Preview
@Composable
fun SuspensionScreenPreview() {
   SuspensionScreen()
}
*/
