package com.ylabz.basepro.ashbike.mobile.ui.navigation.graphs

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation // Import for nested navigation
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiRoute
import com.ylabz.basepro.applications.bike.features.main.ui.BikeViewModel
import com.ylabz.basepro.applications.bike.features.settings.ui.SettingsUiRoute
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsUIRoute
import com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailScreen
import com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailViewModel
import com.ylabz.basepro.applications.bike.features.trips.ui.components.haversineMeters
import com.ylabz.basepro.core.ui.BikeScreen // For screen route constants
import com.ylabz.basepro.core.ui.NavigationCommand
import com.ylabz.basepro.core.util.Logging
import com.ylabz.basepro.feature.places.ui.CoffeeShopEvent
import com.ylabz.basepro.feature.places.ui.CoffeeShopUIState
import com.ylabz.basepro.feature.places.ui.CoffeeShopViewModel

// Define BikeNavGraph as an extension function on NavGraphBuilder
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun NavGraphBuilder.bikeNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    bikeViewModel: BikeViewModel
) {
    val TAG = Logging.getTag(this::class.java)

    // 1) Home Tab Nested Graph - CORRECTED
    navigation(startDestination = BikeScreen.HomeBikeScreen.route, route = AshBikeTabRoutes.HOME_ROOT) {
        composable(BikeScreen.HomeBikeScreen.route) {
            BikeUiRoute(
                modifier = modifier,
                navTo = { command ->
                    when (command) {
                        is NavigationCommand.To -> navHostController.navigate(command.route)
                        is NavigationCommand.ToTab -> {
                            navHostController.navigate(command.route) {
                                popUpTo(navHostController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },
                viewModel = bikeViewModel,
            )
        }
        // Add other destinations specific to the Home tab inside this navigation block if needed
    }

    // 2) Trips Tab Nested Graph
    navigation(startDestination = BikeScreen.TripBikeScreen.route, route = AshBikeTabRoutes.TRIPS_ROOT) {
        composable(BikeScreen.TripBikeScreen.route) {
            TripsUIRoute(
                modifier = modifier,
                navTo = { rideId ->
                    navHostController.navigate(BikeScreen.RideDetailScreen.createRoute(rideId))
                }
            )
        }

        composable(
            route = BikeScreen.RideDetailScreen.route, 
            arguments = listOf(navArgument("rideId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vm: RideDetailViewModel = hiltViewModel(backStackEntry)
            val rideWithLocs by vm.rideWithLocations.collectAsState()
            val cafeViewModel = hiltViewModel<CoffeeShopViewModel>()
            val cafeUiState by cafeViewModel.uiState.collectAsState()
            Logging.d(TAG, "Recomposing RideDetailScreen with cafeUiState: ${cafeUiState::class.java.simpleName}")
            val findCafesAction = {
                val locations = rideWithLocs?.locations
                if (!locations.isNullOrEmpty()) {
                    val centerLat = locations.map { it.lat }.average()
                    val centerLng = locations.map { it.lng }.average()
                    val routeRadius = locations.maxOfOrNull { location ->
                        haversineMeters(centerLat, centerLng, location.lat, location.lng)
                    } ?: 0.0
                    val searchRadius = (routeRadius + 100.0).coerceIn(200.0, 1500.0)
                    Logging.i(TAG, "User requested cafes. Searching with center ($centerLat, $centerLng) and dynamic radius ${searchRadius}m")
                    cafeViewModel.onEvent(
                        CoffeeShopEvent.FindCafesInArea(
                            latitude = centerLat,
                            longitude = centerLng,
                            radius = searchRadius
                        )
                    )
                } else {
                    Logging.w(TAG, "User requested cafes, but ride location data is not available.")
                }
            }
            val coffeeShops = when (val state = cafeUiState) {
                is CoffeeShopUIState.Success -> state.coffeeShops
                else -> emptyList()
            }
            RideDetailScreen(
                modifier = Modifier.fillMaxSize(),
                rideWithLocs = rideWithLocs,
                coffeeShops = coffeeShops,
                onFindCafes = findCafesAction,
                onEvent = { event -> vm.onEvent(event) },
            )
        }
    }

    // 3) Settings Tab Nested Graph
    val settingsBaseRoute = BikeScreen.SettingsBikeScreen.route
    // Use the constant from BikeScreen.kt
    val cardArgName = BikeScreen.SettingsBikeScreen.ARG_CARD_TO_EXPAND
    val settingsRoutePattern = "$settingsBaseRoute?$cardArgName={$cardArgName}"

    // Ensure startDestination can handle the pattern (it does with nullable arg)
    navigation(startDestination = settingsRoutePattern, route = AshBikeTabRoutes.SETTINGS_ROOT) {
        composable(
            route = settingsRoutePattern, // Use the constructed pattern
            arguments = listOf(
                navArgument(cardArgName) { // Use the constant
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val cardToExpand = backStackEntry.arguments?.getString(cardArgName) // Use the constant
            SettingsUiRoute(
                modifier = modifier,
                navTo = { path -> navHostController.navigate(path) },
                initialCardKeyToExpand = cardToExpand
            )
        }
    }

}
