package com.ylabz.basepro.applications.bike.ui.navigation.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ylabz.basepro.applications.bike.ui.BikeUiRoute
import com.ylabz.basepro.applications.bike.ui.navigation.graphs.bikeNavGraph
import com.ylabz.basepro.core.ui.BIKE
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.core.ui.ROOT
import com.ylabz.basepro.settings.ui.SettingsUiRoute


@Composable
fun RootNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    startDestination: String = "bike_nav_graph"
) {
    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = startDestination,
        route = ROOT,
    ) {
        bikeNavGraph(
            modifier = modifier,
            navHostController = navHostController
        )
    }
}

/*
composable(route = MAIN) {
            MainScreen(navHostController)
        }
 */