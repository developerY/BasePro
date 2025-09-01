package com.ylabz.basepro.applications.photodo.ui.navigation.graphs

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.PhotoDoListUiRoute
import com.ylabz.basepro.applications.photodo.features.settings.ui.PhotoDoSettingsUiRoute
import com.ylabz.basepro.core.ui.RxDigitaScreen
import com.ylabz.basepro.core.util.Logging

// Define rxDigitaNavGraph as an extension function on NavGraphBuilder
// @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun NavGraphBuilder.photodoNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    //bikeViewModel: BikeViewModel // <<< MODIFIED LINE: Added bikeViewModel parameter
) {

    val TAG = Logging.getTag(this::class.java)

    // 1) Home Tab
    composable(RxDigitaScreen.HomeRxDigitaScreen.route) { // Use RxDigitaScreen
        PhotoDoHomeUiRoute(
            modifier = modifier,
            navTo = { path -> navHostController.navigate(path) }
        )
    }

    // 2) Trips Tab (Assuming MedlistUiRoute for "Ride" or "Trip")
    composable(RxDigitaScreen.TripRxDigitaScreen.route) { // Use RxDigitaScreen
        PhotoDoListUiRoute(
            modifier = modifier,
            navTo = { path -> navHostController.navigate(path) }
        )
    }
    // 3) Settings Tab
    composable(RxDigitaScreen.SettingsRxDigitaScreen.route) { // Use RxDigitaScreen
        PhotoDoSettingsUiRoute(
            modifier = modifier,
            navTo = { path -> navHostController.navigate(path) }
        )
    }

    /* 4) Ride Detail Screen
    composable(
        route = BikeScreen.RideDetailScreen.route,
        arguments = listOf(navArgument("rideId") { type = NavType.StringType })
    ) { backStackEntry ->
    }*/

}
