package com.ylabz.basepro.ashbike.wear.app

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.ylabz.basepro.ashbike.wear.presentation.screens.history.RideDetailScreen
import com.ylabz.basepro.ashbike.wear.presentation.screens.ride.WearBikeScreen
import com.ylabz.basepro.ashbike.wear.presentation.screens.ride.WearBikeViewModel

// 1. Root App Component
@Composable
fun AshBikeApp(
    viewModel: WearBikeViewModel = hiltViewModel() // <--- Hilt automatically provides this
) {
    val navController = rememberSwipeDismissableNavController()

    // AppScaffold is the root container for M3 Wear apps
    // Handles the clock (TimeText) so it stays put while you swipe pages below it.
    AppScaffold(
        /*timeText = {
            // The M3 TimeText component defaults to curved text on round screens
            TimeText { time() }
        }*/
    ) {
        // 3. NAV HOST: The Content Container
        // Handles the "Swipe Right to Go Back" gesture automatically.
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "active_ride"
        ) {

            // --- SCREEN 1: The Ride (Pager) ---
            composable("active_ride") {
                // We will put the Gauge + Stats Pager here next
                WearBikeScreen(navController)
                //ActiveRideRoute(navController)
            }

            // 2. THE NEW DETAIL SCREEN
            composable("ride_detail/{rideId}") { backStackEntry ->
                val rideId = backStackEntry.arguments?.getString("rideId")
                if (rideId != null) {
                    RideDetailScreen(
                        rideId = rideId,
                        onDeleteSuccess = { navController.popBackStack() }
                    )
                }
            }

            // --- SCREEN 2: Summary (Example) ---
            composable("summary") {
                // SummaryScreen()
            }
        }
    }
}