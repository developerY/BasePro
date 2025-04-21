package com.ylabz.basepro.applications.bike.ui.navigation.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ylabz.basepro.applications.bike.ui.navigation.graphs.bikeNavGraph
import com.ylabz.basepro.core.ui.BikeScreen

@Composable
fun MainNavGraph(
    modifier:      Modifier,
    navController: NavHostController
) {
    NavHost(
        navController    = navController,
        startDestination = BikeScreen.HomeBikeScreen.route
    ) {
        bikeNavGraph(modifier, navController)
    }
}
