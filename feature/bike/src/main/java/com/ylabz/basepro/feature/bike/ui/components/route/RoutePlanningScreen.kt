package com.ylabz.basepro.feature.bike.ui.components.route

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.feature.bike.ui.components.MapScreen
import com.ylabz.basepro.feature.bike.ui.components.PreferenceSwitch

// The RoutePlanningScreen combines all the elements into one screen.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutePlanningScreen() {
    // Preference toggle states.
    val flatRoute = remember { mutableStateOf(true) }
    val pavedRoads = remember { mutableStateOf(true) }
    val avoidTraffic = remember { mutableStateOf(false) }
    // For demonstration, currentLocation is kept as null.
    val currentLocation = remember { mutableStateOf<LatLng?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Plan Your Route") }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Preferences card with three toggles and icons.
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Route Preferences",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PreferenceSwitch(
                            label = "Flat Route",
                            checked = flatRoute.value,
                            onCheckedChange = { flatRoute.value = it },
                            icon = Icons.Filled.Warning// Terrain
                        )
                        PreferenceSwitch(
                            label = "Paved Roads",
                            checked = pavedRoads.value,
                            onCheckedChange = { pavedRoads.value = it },
                            icon = Icons.Filled.LocationOn //DirectionsCar
                        )
                        PreferenceSwitch(
                            label = "Avoid Heavy Traffic",
                            checked = avoidTraffic.value,
                            onCheckedChange = { avoidTraffic.value = it },
                            icon = Icons.Filled.Warning //Warning
                        )
                    }
                }
                // Map card that expands to fill remaining space.
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    MapScreen(loc = currentLocation.value)
                }
                // Start Navigation button at the bottom.
                Button(
                    onClick = { /* Trigger route planning / navigation */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(48.dp)
                ) {
                    Text("Start Navigation")
                }
            }
        }
    )
}