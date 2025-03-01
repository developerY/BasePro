package com.ylabz.basepro.feature.bike.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutePlanningScreen() {
    // Background gradient
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF58B5EB), Color(0xFF6AD8AC))
    )

    // States for toggles
    val avoidHeavyTraffic = remember { mutableStateOf(false) }
    val preferFlatTerrain = remember { mutableStateOf(false) }
    val preferScenicRoutes = remember { mutableStateOf(false) }
    val enableArNavigation = remember { mutableStateOf(false) }

    // Whether the Preferences card is flipped
    var isPreferencesFlipped by remember { mutableStateOf(false) }

    // Main container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())  // If you want the map to expand fully, remove the scroll
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

            // ▼▼▼ FlipCard usage ▼▼▼
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                // Our custom FlipCard
                FlipCard(
                    isFlipped = isPreferencesFlipped,
                    front = {
                        // FRONT SIDE (Preferences + Toggles)
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Header row (clickable to flip)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isPreferencesFlipped = !isPreferencesFlipped },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Preferences",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    painter = painterResource(android.R.drawable.arrow_up_float),
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Toggles
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
                        }
                    },
                    back = {
                        // BACK SIDE
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isPreferencesFlipped = !isPreferencesFlipped },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Back of Card",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    painter = painterResource(android.R.drawable.arrow_down_float),
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Here is where you could show advanced settings, tips, or additional info!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                        }
                    }
                )
            }
            // ▲▲▲ END FlipCard usage ▲▲▲

            Spacer(modifier = Modifier.height(12.dp))

            // Map
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

            Spacer(modifier = Modifier.height(16.dp))

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

// A reusable switch row for preferences
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

/**
 * A composable that flips between a front and back side.
 *
 * @param isFlipped Whether to show the back side (true) or the front (false).
 * @param front The composable for the front side.
 * @param back The composable for the back side.
 */
@Composable
fun FlipCard(
    isFlipped: Boolean,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit
) {
    // Animate from 0° to 180° when flipping
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600)
    )

    // Increase cameraDistance to reduce distortion
    val cameraDistance = 8 * LocalDensity.current.density

    Box(
        modifier = Modifier.graphicsLayer {
            this.cameraDistance = cameraDistance
            rotationY = rotation
        }
    ) {
        // Show front if rotation <= 90, else show back
        if (rotation <= 90f) {
            front()
        } else {
            // Rotate the back side 180° so it looks correct when flipped
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                back()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoutePlanningScreen() {
    MaterialTheme {
        RoutePlanningScreen()
    }
}




