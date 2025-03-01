package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.feature.bike.ui.BikeEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeHomeScreen(
    modifier: Modifier = Modifier,
    settings: Map<String, List<String>>,
    location: LatLng?,
    onEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bike Home") }
            )
        },
        bottomBar = {
            // Bottom bar with three buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { navTo("bike") }) {
                    Text("Action 1")
                }
                Button(onClick = { /* TODO: Handle Action 2 */ }) {
                    Text("Action 2")
                }
                Button(onClick = { /* TODO: Handle Action 3 */ }) {
                    Text("Action 3")
                }
            }
        },
        content = { innerPadding ->
            // Main content of the screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Bike Home Content")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewBikeHomeScreen() {
    val sampleSettings = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
    )


    MaterialTheme {
        BikeHomeScreen(
            settings = sampleSettings,
            onEvent = {},
            location = LatLng(0.0,0.0),
            navTo = {} // No-op for preview
        )
    }
}
