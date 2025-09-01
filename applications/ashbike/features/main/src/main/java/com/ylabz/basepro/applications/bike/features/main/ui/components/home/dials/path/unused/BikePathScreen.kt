package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path.unused

//import androidx.compose.ui.tooling.preview.Preview
import android.Manifest
import android.R
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikePathScreen(
    modifier: Modifier = Modifier,
    settings: Map<String, List<String>>,
    location: LatLng?,
    //onEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit
) {

     val context = LocalContext.current
    // Create a launcher for requesting permissions.
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // You can log or handle the permissions result if needed.
    }

    // Launch the permissions request when the composable is first composed.
    LaunchedEffect(Unit) {
        permissionsLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Check if permissions are granted.
    val fineLocationGranted = remember { mutableStateOf(false) }
    val coarseLocationGranted = remember { mutableStateOf(false) }

    // In a real app, you should observe the current permission state.
    // For demo purposes, let's assume permissions are granted if the check passes:
    LaunchedEffect(Unit) {
        fineLocationGranted.value =
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        coarseLocationGranted.value =
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    fineLocationGranted.value || coarseLocationGranted.value

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
                                    painterResource(R.drawable.arrow_up_float)
                                else
                                    painterResource(R.drawable.arrow_down_float),
                                contentDescription = "Expand or Collapse",
                                tint = Color.Gray
                            )
                        }
                        IconButton(onClick = { isPreferencesFlipped = !isPreferencesFlipped }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_menu_rotate),
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

/*
@Preview(showBackground = true)
@Composable
fun PreviewRoutePlanningScreen() {
    val sampleSettings = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
    )

    MaterialTheme {
        BikePathScreen(
            settings = sampleSettings,
            //onEvent = {},
            location = LatLng(0.0,0.0),
            navTo = {} // No-op for preview
        )
    }
}
*/




