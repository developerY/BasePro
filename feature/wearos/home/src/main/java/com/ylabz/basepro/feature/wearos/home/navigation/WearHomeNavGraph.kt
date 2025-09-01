package com.ylabz.basepro.feature.wearos.home.navigation

import androidx.compose.runtime.Composable
//import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.composable
import com.ylabz.basepro.feature.wearos.drunkwatch.DrunkWatchRoute
import com.ylabz.basepro.feature.wearos.health.ui.WearHealthRoute
import com.ylabz.basepro.feature.wearos.home.presentation.WearHomeRoute
import com.ylabz.basepro.feature.wearos.sleepwatch.SleepWatchRoute

@Composable
fun WearNavGraph() {
    rememberSwipeDismissableNavController()

    // Wear scaffolding (TimeText, Vignette, etc.)
    AppScaffold {
        val navController = rememberSwipeDismissableNavController()
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
            composable(route = WearScreen.Drunk.route) {
                DrunkWatchRoute(navController = navController)
            }
        }
    }
}

@Composable
fun WearHomeNavGraphOirg() {
    val navController = rememberSwipeDismissableNavController()

    // Wear scaffolding (TimeText, Vignette, etc.)
    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(VignettePosition.TopAndBottom) }
    ) {
        // No padding needed here in Material 3
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
            composable(route = WearScreen.Drunk.route) {
                DrunkWatchRoute(navController = navController)
            }
        }
    }
}

/*
@Preview
@Composable
private fun PreviewWearHomeNavGraph() {
     WearNavGraph()
}

 */
