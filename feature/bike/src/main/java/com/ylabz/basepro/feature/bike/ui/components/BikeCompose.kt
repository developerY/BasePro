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

    // Collapsible and flip states for Preferences
    var isPreferencesExpanded by remember { mutableStateOf(true) }
    var isPreferencesFlipped by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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

            // ▼▼▼ Preferences Card with collapse & flip ▼▼▼
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column {
                    // Header row with two icons: collapse & flip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Preferences",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        // Collapse/Expand icon
                        IconButton(onClick = { isPreferencesExpanded = !isPreferencesExpanded }) {
                            Icon(
                                painter = if (isPreferencesExpanded)
                                    painterResource(android.R.drawable.arrow_up_float)
                                else
                                    painterResource(android.R.drawable.arrow_down_float),
                                contentDescription = "Expand or Collapse",
                                tint = Color.Gray
                            )
                        }
                        // Flip icon
                        IconButton(onClick = { isPreferencesFlipped = !isPreferencesFlipped }) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_rotate),
                                contentDescription = "Flip Card",
                                tint = Color.Gray
                            )
                        }
                    }

                    // FlipCard composable to handle the 3D flip animation
                    FlipCard(
                        isFlipped = isPreferencesFlipped,
                        front = {
                            // FRONT side of card
                            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                                if (isPreferencesExpanded) {
                                    // Show toggles
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
                                } else {
                                    // Collapsed
                                    Text(
                                        text = "Preferences are collapsed.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        },
                        back = {
                            // BACK side of card
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Advanced Settings",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Place your advanced or additional settings here!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )
                            }
                        }
                    )
                }
            }
            // ▲▲▲ END of Preferences Card ▲▲▲

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
 * A composable that flips between a front and back side with a 3D rotation animation.
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
        if (rotation <= 90f) {
            // FRONT
            front()
        } else {
            // BACK (rotate 180° so text isn’t reversed)
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




