package com.ylabz.basepro.applications.bike.ui.navigation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Recommended for LTR/RTL automatic mirroring
// import androidx.compose.material.icons.filled.ArrowBack // Keep one, automirrored is usually better
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource // Import for string resources
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.applications.bike.R // Import R class from your ashbike module

@Composable
fun TopBarForCurrentRoute(navController: NavHostController) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    // Determine the title resource ID based on the current route
    // If no specific title, AppTopBar might render without one or with a default,
    // or you could choose not to render a TopAppBar at all.
    val titleResId: Int? = when (currentRoute) {
        BikeScreen.HomeBikeScreen.route     -> R.string.top_bar_title_ashbike
        BikeScreen.TripBikeScreen.route     -> R.string.top_bar_title_trips
        BikeScreen.SettingsBikeScreen.route -> R.string.top_bar_title_settings
        // For RideDetailScreen, the title is handled by DetailTopBar,
        // but if AppTopBar were to handle it, it would be here.
        else                                -> null // Or a default title resource
    }

    when (currentRoute) {
        BikeScreen.HomeBikeScreen.route,
        BikeScreen.TripBikeScreen.route,
        BikeScreen.SettingsBikeScreen.route -> {
            // Only render AppTopBar if titleResId is not null
            titleResId?.let { resId ->
                AppTopBar(title = stringResource(id = resId))
            }
        }
        BikeScreen.RideDetailScreen.route   -> DetailTopBar(onBack = navController::popBackStack)
        else                                -> { /* No TopAppBar or a default one */ }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String) { // Title is now passed already localized
    TopAppBar(title = { Text(title) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.top_bar_title_ride_details)) }, // Localized title
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, // Use AutoMirrored version
                    contentDescription = stringResource(id = R.string.top_bar_back_button_description) // Localized description
                )
            }
        }
    )
}

