package com.ylabz.basepro.feature.bike.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.feature.bike.ui.BikeEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeCompose(
    modifier: Modifier = Modifier,
    settings: Map<String, List<String>>, // Each setting now has a list of options
    onEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit // Navigation callback for FAB
) {



    // States for toggles (Switches)
    val avoidHeavyTraffic = remember { mutableStateOf(false) }
    val preferFlatTerrain = remember { mutableStateOf(false) }
    val preferScenicRoutes = remember { mutableStateOf(false) }
    val enableArNavigation = remember { mutableStateOf(false) }

    // Optional: Scaffold for a TopAppBar or other Material components
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Route Planning") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 5.1. Customized Route Generation
            SectionHeader(title = "5.1. Customized Route Generation")

            // 5.1.1. Preference-Based Routing
            SubsectionHeader(title = "5.1.1. Preference-Based Routing")
            Text(
                text = "• Terrain Preferences: Selecting routes based on difficulty (flat routes or hill climbs)\n" +
                        "• Surface Types: Filtering routes by surface (paved roads, gravel paths, trails)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Toggles for preferences
            PreferenceSwitch(
                label = "Avoid Heavy Traffic",
                checked = avoidHeavyTraffic.value,
                onCheckedChange = { avoidHeavyTraffic.value = it }
            )
            PreferenceSwitch(
                label = "Prefer Flat Terrain",
                checked = preferFlatTerrain.value,
                onCheckedChange = { preferFlatTerrain.value = it }
            )
            PreferenceSwitch(
                label = "Prefer Scenic Routes",
                checked = preferScenicRoutes.value,
                onCheckedChange = { preferScenicRoutes.value = it }
            )

            // 5.1.2. Safety and Comfort Factors
            SubsectionHeader(title = "5.1.2. Safety and Comfort Factors")
            Text(
                text = "• Traffic Density Analysis: Avoid high-traffic areas for safer rides\n" +
                        "• Lighting Conditions: Consider time of day and street lighting for visibility",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Map placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Map Placeholder")
            }

            // AR Navigation (Beta)
            PreferenceSwitch(
                label = "Enable AR Navigation (Beta)",
                checked = enableArNavigation.value,
                onCheckedChange = { enableArNavigation.value = it }
            )

            // 5.2. Real-Time Route Adjustments
            SectionHeader(title = "5.2. Real-Time Route Adjustments")

            // 5.2.1. Dynamic Re-Routing
            SubsectionHeader(title = "5.2.1. Dynamic Re-Routing")
            Text(
                text = "• Incident Avoidance: Automatically adjust routes to avoid accidents or construction zones\n" +
                        "• Pace Adjustments: Modify the route to accommodate changes in speed or delays",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // 5.2.2. Interactive Map Features
            SubsectionHeader(title = "5.2.2. Interactive Map Features")
            Text(
                text = "• Points of Interest (POIs): Display rest stops, viewpoints, and amenities\n" +
                        "• Community Updates: Show real-time updates from other cyclists about route conditions",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Start Navigation button
            Button(
                onClick = { /* Handle navigation start */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Start Navigation")
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun SubsectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun PreferenceSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BikeComposePreview() {
    val sampleSettings = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
    )

    BikeCompose(
        settings = sampleSettings,
        onEvent = {},
        navTo = {} // No-op for preview
    )
}


