package com.ylabz.basepro.feature.bike.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutePlanningScreen() {
    // Gradient background
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF58B5EB), Color(0xFF6AD8AC))
    )

    // States for toggles
    val avoidHeavyTraffic = remember { mutableStateOf(false) }
    val preferFlatTerrain = remember { mutableStateOf(false) }
    val preferScenicRoutes = remember { mutableStateOf(false) }
    val enableArNavigation = remember { mutableStateOf(false) }

    // Collapsible state for Preferences card
    var isPreferencesExpanded by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                // Reduced overall screen padding
                .padding(12.dp)
        ) {
            // Title
            Text(
                text = "Route Planning",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Collapsible Card for Preferences
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column {
                    // Card header (clickable to collapse/expand)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPreferencesExpanded = !isPreferencesExpanded }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Preferences",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            painter = if (isPreferencesExpanded) {
                                painterResource(android.R.drawable.arrow_up_float)
                            } else {
                                painterResource(android.R.drawable.arrow_down_float)
                            },
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }

                    // Only show switches if expanded
                    if (isPreferencesExpanded) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
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
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Map card (increased height to 300dp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Map Placeholder",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AR Navigation (Beta)
            Text(
                text = "AR Navigation (Beta)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            PreferenceSwitch(
                label = "Enable AR Navigation",
                checked = enableArNavigation.value,
                onCheckedChange = { enableArNavigation.value = it }
            )

            // Push button to bottom
            Spacer(modifier = Modifier.weight(1f))

            // Start Navigation button
            Button(
                onClick = { /* Start navigation action */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0066FF),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Start Navigation",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
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
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            color = Color.Black
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF4CAF50)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoutePlanningScreen() {
    MaterialTheme {
        RoutePlanningScreen()
    }
}



