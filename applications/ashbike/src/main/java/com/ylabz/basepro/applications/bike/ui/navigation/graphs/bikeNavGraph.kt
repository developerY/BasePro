package com.ylabz.basepro.applications.bike.ui.navigation.graphs

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ylabz.basepro.applications.bike.ui.components.settings.SettingsRoute
import com.ylabz.basepro.core.ui.BikeScreen
import androidx.navigation.navigation
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsUIRoute
import com.ylabz.basepro.applications.bike.features.trips.ui.components.DetailsTripRoute
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
            TripsUIRoute(
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

        // master / detail views for tasks
        composable(
            route = "details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("id")
            itemId?.let {
                DetailsTripRoute(
                    navController = navHostController,
                    itemId = it
                )
            }
        }
    }
}