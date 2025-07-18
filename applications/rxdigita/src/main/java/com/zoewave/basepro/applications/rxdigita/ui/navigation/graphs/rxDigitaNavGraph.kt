package com.zoewave.basepro.applications.rxdigita.ui.navigation.graphs

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ylabz.basepro.core.ui.RxDigitaScreen // Import the correct Screen definitions
import com.ylabz.basepro.core.util.Logging

// Define rxDigitaNavGraph as an extension function on NavGraphBuilder
// @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun NavGraphBuilder.rxDigitaNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    //bikeViewModel: BikeViewModel // <<< MODIFIED LINE: Added bikeViewModel parameter
) {

    val TAG = Logging.getTag(this::class.java)

    // 1) Home Tab
    composable(RxDigitaScreen.HomeRxDigitaScreen.route) { // Use RxDigitaScreen
        /*RxDigitaUiRoute(
            modifier = modifier,
            navTo = { path -> navHostController.navigate(path) },
            //viewModel = bikeViewModel // <<< MODIFIED LINE: Pass the bikeViewModel instance
        )*/
        // Placeholder for content, you'll need to add your actual Home screen composable here
        Text("Home RxDigita Screen Content")
    }

    // 2) Trips Tab
    composable(RxDigitaScreen.TripRxDigitaScreen.route) { // Use RxDigitaScreen
        Text("Trips RxDigita Screen Content")
    }
    // 3) Settings Tab
    composable(RxDigitaScreen.SettingsRxDigitaScreen.route) { // Use RxDigitaScreen
        Text("Settings RxDigita Screen Content")
    }

}
