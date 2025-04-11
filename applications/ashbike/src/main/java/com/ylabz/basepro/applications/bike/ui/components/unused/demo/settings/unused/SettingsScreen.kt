package com.ylabz.basepro.applications.bike.ui.components.unused.demo.settings.unused

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navTo : (String) -> Unit,
    // navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // One list item for "App Preferences"
            SettingsListItem(
                title = "App Preferences",
                onClick = {
                    // Navigate to a separate screen (AppPreferencesScreen)
                    navTo("appPreferences")
                }
            )

            // One list item for "Bike Configuration"
            SettingsListItem(
                title = "Bike Configuration",
                onClick = {
                    // Navigate to a separate screen (BikeConfigurationScreen)
                    navTo("bikeConfiguration")
                }
            )
        }
    }
}

@Composable
fun SettingsListItem(
    title: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go to $title"
            )
        },
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
    HorizontalDivider()
}

// Example of the two sub-screens:

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPreferencesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("App Preferences") })
        }
    ) { innerPadding ->
        // Your App Preferences UI here
        // e.g. theme toggle, notification settings, etc.
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            Text(text = "All your app-level settings go here.")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeConfigurationScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Bike Configuration") })
        }
    ) { innerPadding ->
        // Your Bike Configuration UI here
        // e.g. motor assistance level, gear ratio, brake calibration, etc.
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            Text(text = "All your bike-level settings go here.")
        }
    }
}


// Preview
@Preview
@Composable
fun GearingScreenPreview() {
    SettingsScreen(navTo = {})
}
