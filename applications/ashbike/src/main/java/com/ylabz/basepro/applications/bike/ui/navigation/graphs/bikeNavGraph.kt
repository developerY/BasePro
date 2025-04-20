package com.ylabz.basepro.applications.bike.ui.navigation.graphs

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsUIRoute
import com.ylabz.basepro.applications.bike.ui.BikeUiRoute

import com.ylabz.basepro.applications.bike.features.settings.ui.SettingsUiRoute
import com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailScreen

// Define BikeNavGraph as an extension function on NavGraphBuilder
fun NavGraphBuilder.bikeNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController
) {
    // 1) Home Tab
    composable(BikeScreen.HomeBikeScreen.route) {
        BikeUiRoute(
            modifier = modifier,
            navTo    = { path -> navHostController.navigate(path) }
        )
    }

    // 2) Trips Tab
    composable(BikeScreen.TripBikeScreen.route) {
        TripsUIRoute(
            modifier = modifier,
            navTo = { path ->
                // now navTo takes a route string, not an ID
                navHostController.navigate(path)
            }
        )
    }
    // 3) Settings Tab
    composable(BikeScreen.SettingsBikeScreen.route) {
        SettingsUiRoute(
            modifier = modifier,
            navTo    = { path -> navHostController.navigate(path) }
        )
    }

    // 4) Ride Detail (flat, not nested)
    composable(
        route      = BikeScreen.RideDetailScreen.route,        // "ride/{rideId}"
        arguments  = listOf(navArgument("rideId") { type = NavType.StringType })
    ) { backStackEntry ->
        val rideId = backStackEntry.arguments!!.getString("rideId")!!
        RideDetailScreen(
            rideId = rideId,
            onBack = { navHostController.popBackStack() }
        )
    }
}
