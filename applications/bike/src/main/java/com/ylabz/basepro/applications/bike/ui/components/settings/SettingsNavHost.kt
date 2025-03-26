package com.ylabz.basepro.applications.bike.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.applications.bike.ui.components.demo.settings.SettingsScreenEx
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Modifier


sealed class SettingsRoute(val route: String) {
    object Main : SettingsRoute("settings_main")
    object AdvancedBike : SettingsRoute("settings_bike_advanced")
    // Add more if needed (e.g. "app_preferences", "about", etc.)
}


@Composable
fun SettingsNavHost(
    modifier: Modifier = Modifier,
    onBack: () -> Unit  // If you need to communicate “go back” to the parent
) {
    // Local NavController just for settings
    val navController = rememberNavController()
    val navTo : (String) -> Unit = { route -> navController.navigate(route) }

    NavHost(
        navController = navController,
        startDestination = SettingsRoute.Main.route
    ) {
        // 1) Main Settings Screen
        composable(SettingsRoute.Main.route) {
            SettingsScreenEx(
                navTo = navTo,
                // onBack = onBack
            )
        }

        // 2) Advanced Bike Screen
        composable(SettingsRoute.AdvancedBike.route) {
            GearingScreen(
                modifier = modifier,
            )
            /*AdvancedBikeSettingsScreen(
                onBackClick = {
                    // Pop back to the main settings
                    //navController.popBackStack()
                }
            )*/
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedBikeSettingsScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced Bike Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Advanced Bike Settings go here...")
        }
    }
}

