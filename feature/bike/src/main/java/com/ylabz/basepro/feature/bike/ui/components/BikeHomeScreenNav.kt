package com.ylabz.basepro.feature.bike.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.feature.bike.ui.BikeEvent
import com.ylabz.basepro.feature.bike.ui.components.Settiings.BikeSettingsScreen
import com.ylabz.basepro.feature.bike.ui.components.home.BikeNavItem
import com.ylabz.basepro.feature.bike.ui.components.route.BikeRoutesScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeHomeScreenNav(
    modifier: Modifier = Modifier,
    settings: Map<String, List<String>>,
    location: LatLng?,
    onEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit
) {
    // Track which bottom nav item is selected
    var selectedItem by remember { mutableStateOf<BikeNavItem>(BikeNavItem.Home) }
    val sampleSettings = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bike Home") }
            )
        },
        // Main content of the screen
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                when (selectedItem) {
                    BikeNavItem.Home -> BikeHomeScreenNav(
                        settings = sampleSettings,
                        onEvent = {},
                        location = LatLng(0.0,0.0),
                        navTo = {} // No-op for preview
                    )
                    BikeNavItem.Routes -> BikeRoutesScreen()
                    BikeNavItem.Settings -> BikeSettingsScreen()
                }
            }
        },
        // Bottom navigation bar (Material 3: NavigationBar)
        bottomBar = {
            NavigationBar {
                /*BikeNavItem.allItems.forEach { item ->
                    // If item is null (should not happen), skip it.
                    if (item == null) return@forEach
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label ?: "Unknown") },
                        label = { Text(item.label ?: "Unknown") },
                        selected = selectedItem == item,
                        onClick = { selectedItem = item }
                    )
                }*/
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewBikeHomeScreenNav() {
    val sampleSettings = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
    )


    MaterialTheme {
        BikeHomeScreenNav(
            settings = sampleSettings,
            onEvent = {},
            location = LatLng(0.0,0.0),
            navTo = {} // No-op for preview
        )
    }
}
