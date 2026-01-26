package com.rxdigita.basepro.applications.rxtrack.ui.navigation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ylabz.basepro.core.ui.BikeScreen

@Composable
fun TopBarForCurrentRoute(navController: NavHostController) {
    val backStack by navController.currentBackStackEntryAsState()
    when (val route = backStack?.destination?.route) {
        BikeScreen.HomeBikeScreen.route -> AppTopBar(title = "AshBike")
        BikeScreen.TripBikeScreen.route -> AppTopBar(title = "Trips")
        BikeScreen.SettingsBikeScreen.route -> AppTopBar(title = "Settings")
        BikeScreen.RideDetailScreen.route -> DetailTopBar(onBack = navController::popBackStack)
        else -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String) {
    TopAppBar(title = { Text(title) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Ride Details") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    )
}
