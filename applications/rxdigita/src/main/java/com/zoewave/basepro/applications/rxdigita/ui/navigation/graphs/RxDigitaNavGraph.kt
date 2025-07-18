package com.zoewave.basepro.applications.rxdigita.ui.navigation.graphs

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.core.util.Logging

// Define BikeNavGraph as an extension function on NavGraphBuilder
// @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun NavGraphBuilder.RxDigitaNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    //bikeViewModel: BikeViewModel // <<< MODIFIED LINE: Added bikeViewModel parameter
) {

    val TAG = Logging.getTag(this::class.java)

    // 1) Home Tab
    composable(BikeScreen.HomeBikeScreen.route) {
        /*RxDigitaUiRoute(
            modifier = modifier,
            navTo = { path -> navHostController.navigate(path) },
            //viewModel = bikeViewModel // <<< MODIFIED LINE: Pass the bikeViewModel instance
        )*/
    }

    // 2) Trips Tab
    composable(BikeScreen.TripBikeScreen.route) {
        Text("hi")
    }
    // 3) Settings Tab
    composable(BikeScreen.SettingsBikeScreen.route) {
        Text("Settings")
    }

}
