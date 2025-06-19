package com.ylabz.basepro.applications.bike.ui.navigation.graphs

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
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
import com.ylabz.basepro.applications.bike.features.trips.ui.components.haversineMeters
import com.ylabz.basepro.core.util.Logging
import com.ylabz.basepro.feature.places.ui.CoffeeShopEvent
import com.ylabz.basepro.feature.places.ui.CoffeeShopUIState
import com.ylabz.basepro.feature.places.ui.CoffeeShopViewModel

// Define BikeNavGraph as an extension function on NavGraphBuilder
@RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
fun NavGraphBuilder.bikeNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController
) {

    val TAG = Logging.getTag(this::class.java)

    // 1) Home Tab
    composable(BikeScreen.HomeBikeScreen.route) {
        BikeUiRoute(
            modifier = modifier,
            navTo = { path -> navHostController.navigate(path) }
        )
    }

    // 2) Trips Tab
    composable(BikeScreen.TripBikeScreen.route) {

        TripsUIRoute(
            modifier = modifier,
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

    // 4) Ride Detail Screen
    composable(
        route = BikeScreen.RideDetailScreen.route,
        arguments = listOf(navArgument("rideId") { type = NavType.StringType })
    ) { backStackEntry ->
        val vm: RideDetailViewModel = hiltViewModel(backStackEntry)
        val rideWithLocs by vm.rideWithLocations.collectAsState()

        val cafeViewModel = hiltViewModel<CoffeeShopViewModel>()
        val cafeUiState by cafeViewModel.uiState.collectAsState()

        // Key part: When does this fire?
        LaunchedEffect(rideWithLocs) {
            // Log if rideWithLocs is null or not
            if (rideWithLocs == null) {
                Logging.w(TAG, "LaunchedEffect fired, but rideWithLocs is NULL.")
                return@LaunchedEffect
            }

            rideWithLocs?.locations?.firstOrNull()?.let { location ->
                Logging.i(TAG, "Ride data loaded. Triggering FindCafesNear for lat=${location.lat}, lon=${location.lng}")
                cafeViewModel.onEvent(
                    CoffeeShopEvent.FindCafesNear(
                        latitude = location.lat,
                        longitude = location.lng
                    )
                )
            } ?: Logging.w(TAG, "Ride data loaded, but location list is empty.")
        }

        // Log the raw state of the cafe UI
        Logging.d(TAG, "Recomposing with cafeUiState: ${cafeUiState::class.java.simpleName}")

        // This effect triggers when the locations list is available and not empty
        LaunchedEffect(rideWithLocs?.locations) {
            val locations = rideWithLocs?.locations
            if (!locations.isNullOrEmpty()) {

                // --- CALCULATION LOGIC MOVED TO CALLER ---
                val centerLat = locations.map { it.lat }.average()
                val centerLng = locations.map { it.lng }.average()

                // Find the furthest point from the center to define the radius
                val maxDistanceMeters = locations.maxOfOrNull { location ->
                    haversineMeters(centerLat, centerLng, location.lat, location.lng)
                } ?: 250.0 // Default to 250m if calculation fails

                // Use this distance as our search radius, capped to a reasonable limit
                val searchRadius = maxDistanceMeters.coerceAtMost(1000.0) / 2.0// Cap at 1km
                // --- END CALCULATION LOGIC ---

                Logging.i(
                    TAG,
                    "Requesting cafes with center ($centerLat, $centerLng) and radius $searchRadius"
                )

                cafeViewModel.onEvent(
                    CoffeeShopEvent.FindCafesInArea(
                        latitude = centerLat,
                        longitude = centerLng,
                        radius = searchRadius
                    )
                )
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
            onEvent = { event -> vm.onEvent(event) },
        )
    }
}
