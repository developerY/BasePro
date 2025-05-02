package com.ylabz.basepro.applications.bike.ui.navigation.graphs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiRoute
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsUIRoute

import com.ylabz.basepro.applications.bike.features.settings.ui.SettingsUiRoute
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsUIState
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsViewModel
import com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailScreen
import com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailViewModel

// Define BikeNavGraph as an extension function on NavGraphBuilder
fun NavGraphBuilder.bikeNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController
) {
    // 1) Home Tab
    composable(BikeScreen.HomeBikeScreen.route) {
        BikeUiRoute(
            modifier = modifier,
            navTo = { path -> navHostController.navigate(path) }
        )
    }

    // 2) Trips Tab
    composable(BikeScreen.TripBikeScreen.route) {
        val vm: TripsViewModel = hiltViewModel()
        val uiState by vm.uiState.collectAsState()

        TripsUIRoute(
            modifier = modifier,
            uiState  = uiState,
            onEvent  = { event -> vm.onEvent(event) },
            navTo    = { rideId ->
                navHostController.navigate(
                    BikeScreen.RideDetailScreen.createRoute(rideId)
                )
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
        route     = BikeScreen.RideDetailScreen.route,
        arguments = listOf(navArgument("rideId") { type = NavType.StringType })
    ) { backStackEntry ->
        val vm: RideDetailViewModel = hiltViewModel(backStackEntry)
        val vmT: TripsViewModel = hiltViewModel(backStackEntry)
        val rideWithLocs by vm.rideWithLocations.collectAsState()

        RideDetailScreen(
            modifier = Modifier.fillMaxSize(),
            rideWithLocs = rideWithLocs,
            onEvent = { event -> vmT.onEvent(event) },
        )
    }
}
