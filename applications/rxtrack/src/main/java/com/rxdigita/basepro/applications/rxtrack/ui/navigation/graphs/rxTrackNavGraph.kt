package com.rxdigita.basepro.applications.rxtrack.ui.navigation.graphs

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rxdigita.basepro.applications.rxtrack.features.main.ui.MedUiRoute
import com.rxdigita.basepro.applications.rxtrack.features.medlist.ui.MedListUiRoute
import com.rxdigita.basepro.applications.rxtrack.features.settings.ui.SettingsUiRoute
import com.ylabz.basepro.core.ui.RxDigitaScreen
import com.ylabz.basepro.core.util.Logging

// Define rxDigitaNavGraph as an extension function on NavGraphBuilder
// @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun NavGraphBuilder.rxTrackNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    //bikeViewModel: BikeViewModel // <<< MODIFIED LINE: Added bikeViewModel parameter
) {

    Logging.getTag(this::class.java)

    // 1) Home Tab
    composable(RxDigitaScreen.HomeRxDigitaScreen.route) { // Use RxDigitaScreen
        MedUiRoute(
            modifier = modifier,
            navTo = { path -> navHostController.navigate(path) }
        )
    }

    // 2) Trips Tab (Assuming MedlistUiRoute for "Ride" or "Trip")
    composable(RxDigitaScreen.TripRxDigitaScreen.route) { // Use RxDigitaScreen
        MedListUiRoute(
            modifier = modifier,
            navTo = { path -> navHostController.navigate(path) }
        )
    }
    // 3) Settings Tab
    composable(RxDigitaScreen.SettingsRxDigitaScreen.route) { // Use RxDigitaScreen
        SettingsUiRoute(
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
