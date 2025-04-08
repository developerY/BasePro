package com.ylabz.basepro.applications.bike.ui.navigation.graphs

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ylabz.basepro.applications.bike.ui.components.settings.SettingsRoute
import com.ylabz.basepro.core.ui.BikeScreen
import androidx.navigation.navigation
import com.ylabz.basepro.applications.bike.ui.BikeUiRoute
import com.ylabz.basepro.core.ui.BIKE
import com.ylabz.basepro.listings.ui.ListUIRoute
import com.ylabz.basepro.settings.ui.SettingsUiRoute

// Define BikeNavGraph as an extension function on NavGraphBuilder
fun NavGraphBuilder.bikeNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    startDestination: String = BikeScreen.HomeBikeScreen.route
) {
    navigation(
        startDestination = startDestination,
        route = "bike_nav_graph"
    ) {
        composable(BikeScreen.HomeBikeScreen.route) {
            BikeUiRoute(
                modifier = modifier,
                navTo = { path -> navHostController.navigate(path) }
            )
        }
        composable(BikeScreen.TripBikeScreen.route) {
            // track bike ride screen
            ListUIRoute(
                modifier = modifier,
                navTo = { path -> navHostController.navigate(path) }
            )
        }
        composable(BikeScreen.SettingsBikeScreen.route) {
            SettingsUiRoute(
                modifier = modifier,
                navTo = { path -> navHostController.navigate(path) }
            )
        }
    }
}