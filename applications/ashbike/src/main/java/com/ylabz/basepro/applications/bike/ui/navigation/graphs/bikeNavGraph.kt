package com.ylabz.basepro.applications.bike.ui.navigation.graphs

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
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiRoute
import com.ylabz.basepro.applications.bike.features.main.ui.BikeViewModel
import com.ylabz.basepro.applications.bike.features.settings.ui.SettingsUiRoute
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsUIRoute
import com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailScreen
import com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailViewModel
import com.ylabz.basepro.applications.bike.features.trips.ui.components.haversineMeters
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.core.ui.NavigationCommand
import com.ylabz.basepro.core.util.Logging
import com.ylabz.basepro.feature.places.ui.CoffeeShopEvent
import com.ylabz.basepro.feature.places.ui.CoffeeShopUIState
import com.ylabz.basepro.feature.places.ui.CoffeeShopViewModel

// Define BikeNavGraph as an extension function on NavGraphBuilder
@RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
fun NavGraphBuilder.bikeNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    bikeViewModel: BikeViewModel // <<< MODIFIED LINE: Added bikeViewModel parameter
) {

    val TAG = Logging.getTag(this::class.java)

    // 1) Home Tab
    composable(BikeScreen.HomeBikeScreen.route) {
        BikeUiRoute(
            modifier = modifier,
            // Create a navTo lambda that handles our new NavigationCommand
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
            viewModel = bikeViewModel // <<< MODIFIED LINE: Pass the bikeViewModel instance
        )
    }

    // 2) Trips Tab
    composable(BikeScreen.TripBikeScreen.route) {

        TripsUIRoute(
            modifier = modifier,
            navTo    = { rideId ->
                // This is a simple "To" navigation
                navHostController.navigate(
                    BikeScreen.RideDetailScreen.createRoute(rideId)
                )
            }
        )
    }
    // 3) Settings Tab
    val settingsRouteBase = BikeScreen.SettingsBikeScreen.route // Assuming this is "settings_ui_route"
    val cardToExpandArgName = "cardToExpandArg" // Argument name used in BikeViewModel and SettingsUiRoute

    composable(
        route = "$settingsRouteBase?$cardToExpandArgName={$cardToExpandArgName}", // e.g., "settings_ui_route?cardToExpandArg={cardToExpandArg}"
        arguments = listOf(
            navArgument(cardToExpandArgName) {
                type = NavType.StringType
                nullable = true
                defaultValue = null // Explicitly set default for optional argument
            }
        )
    ) { backStackEntry ->
        val cardToExpand = backStackEntry.arguments?.getString(cardToExpandArgName)
        SettingsUiRoute(
            modifier = modifier,
            navTo    = { path -> navHostController.navigate(path) },
            initialCardKeyToExpand = cardToExpand // Pass the extracted argument
        )
    }

    // 4) Ride Detail Screen
    composable(
        route = BikeScreen.RideDetailScreen.route,
        arguments = listOf(navArgument("rideId") { type = NavType.StringType })
    ) { backStackEntry ->
        val vm: RideDetailViewModel = hiltViewModel(backStackEntry)
        val rideWithLocs by vm.rideWithLocations.collectAsState()

        val cafeViewModel = hiltViewModel<CoffeeShopViewModel>()
        val cafeUiState by cafeViewModel.uiState.collectAsState()

        // Log the raw state of the cafe UI
        Logging.d(TAG, "Recomposing with cafeUiState: ${cafeUiState::class.java.simpleName}")



    // The logic to find cafes is now in this lambda.
    // It is passed down to the UI to be called by the button.
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
