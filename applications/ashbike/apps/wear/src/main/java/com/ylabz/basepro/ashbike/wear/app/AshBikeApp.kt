package com.ylabz.basepro.ashbike.wear.app

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.ylabz.basepro.ashbike.wear.presentation.WearBikeScreen
import com.ylabz.basepro.ashbike.wear.presentation.screens.ride.WearBikeViewModel

// 1. Root App Component
@Composable
fun AshBikeApp(
    viewModel: WearBikeViewModel = hiltViewModel() // <--- Hilt automatically provides this
) {
    val navController = rememberSwipeDismissableNavController()

    // AppScaffold is the root container for M3 Wear apps
    AppScaffold {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "ride_screen"
        ) {
            composable("ride_screen") {
                // This screen handles its own permissions and service binding
                WearBikeScreen()
            }
        }
    }
}