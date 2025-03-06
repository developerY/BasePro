package com.ylabz.basepro.feature.bike.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.feature.bike.ui.BikeEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeCompose(
    modifier: Modifier = Modifier,
    settings: Map<String, List<String>>,
    location: LatLng?,
    onEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit
) {
    // Background gradient
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF58B5EB), Color(0xFF6AD8AC))
    )

    // States for toggles
    val avoidHeavyTraffic = remember { mutableStateOf(false) }
    val preferFlatTerrain = remember { mutableStateOf(false) }
    val preferScenicRoutes = remember { mutableStateOf(false) }
    val enableArNavigation = remember { mutableStateOf(false) }

    // States for Preferences card (collapse & flip)
    var isPreferencesExpanded by remember { mutableStateOf(true) }
    var isPreferencesFlipped by remember { mutableStateOf(false) }

    // Outer container (without verticalScroll so weight works)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
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

            // Preferences Card with collapse & flip
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column {
                    // Header row with collapse and flip icons
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
                        IconButton(onClick = { isPreferencesFlipped = !isPreferencesFlipped }) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_rotate),
                                contentDescription = "Flip Card",
                                tint = Color.Gray
                            )
                        }
                    }

                    // FlipCard with front (Preferences) and back (Advanced settings)
                    FlipCard(
                        isFlipped = isPreferencesFlipped,
                        front = {
                            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                                if (isPreferencesExpanded) {
                                    // Switches with icons
                                    PreferencesCardContent(
                                        avoidHeavyTraffic = avoidHeavyTraffic.value,
                                        onAvoidHeavyTrafficChange = { avoidHeavyTraffic.value = it },
                                        preferFlatTerrain = preferFlatTerrain.value,
                                        onPreferFlatTerrainChange = { preferFlatTerrain.value = it },
                                        preferScenicRoutes = preferScenicRoutes.value,
                                        onPreferScenicRoutesChange = { preferScenicRoutes.value = it }
                                    )
                                } else {
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
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Advanced Settings",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
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

            Spacer(modifier = Modifier.height(12.dp))

            // Expanded Map Card (using weight to fill remaining space)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    MapScreen()
                    Text(
                        text = "Bike Map",
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

            // Start Navigation Button
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
fun PreferenceSwitchOld(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // If an icon is provided, display it
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        // The label
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            color = Color.Black
        )

        // The switch
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


@Composable
fun FlipCard(
    isFlipped: Boolean,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit
) {
    // Animate rotation from 0° (front) to 180° (back)
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600)
    )
    val cameraDistance = 8 * LocalDensity.current.density

    Box(modifier = Modifier.graphicsLayer {
        this.cameraDistance = cameraDistance
        rotationY = rotation
    }) {
        if (rotation <= 90f) {
            front()
        } else {
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                back()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoutePlanningScreen() {
    val sampleSettings = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
    )

    MaterialTheme {
        BikeCompose(
            settings = sampleSettings,
            onEvent = {},
            location = LatLng(0.0,0.0),
            navTo = {} // No-op for preview
        )
    }
}





