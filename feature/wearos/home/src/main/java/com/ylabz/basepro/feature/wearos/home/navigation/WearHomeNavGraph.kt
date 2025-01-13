package com.ylabz.basepro.feature.wearos.home.navigation

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.composable
import com.ylabz.basepro.feature.wearos.health.ui.WearHealthRoute
import com.ylabz.basepro.feature.wearos.home.presentation.WearHomeRoute
import com.ylabz.basepro.feature.wearos.sleepwatch.SleepWatchRoute

@Composable
fun WearHomeNavGraph() {
    val navController = rememberSwipeDismissableNavController()

    // Wear scaffolding (TimeText, Vignette, etc.)
    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(VignettePosition.TopAndBottom) }
    ) {
        // Start Destination (or “route”) for your watch
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = WearScreen.Home.route
        ) {
            composable(route = WearScreen.Home.route) {
                WearHomeRoute(navController = navController)
            }
            composable(route = WearScreen.Health.route) {
                WearHealthRoute(navController = navController)
            }
            composable(route = WearScreen.Sleep.route) {
                SleepWatchRoute(navController = navController)
            }
            // Add more watch destinations here...
        }
    }
}
