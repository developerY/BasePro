package com.ylabz.basepro.applications.bike.ui.navigation.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ylabz.basepro.applications.bike.ui.BikeUiRoute
import com.ylabz.basepro.applications.bike.ui.navigation.graphs.bikeNavGraph
import com.ylabz.basepro.applications.bike.ui.navigation.main.MainScreen
import com.ylabz.basepro.core.ui.BIKE
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.core.ui.ROOT
import com.ylabz.basepro.settings.ui.SettingsUiRoute


@Composable
fun RootNavGraph(navController: NavHostController) {
    // In this example, RootNavGraph simply delegates to MainScreen.
    // If you have multiple flows (e.g., auth, bike, settings) you can switch here.
    MainScreen(navController = navController)
}

/*
composable(route = MAIN) {
            MainScreen(navHostController)
        }
 */