package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.HEALTH
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.heatlh.ui.HealthRoute
import com.ylabz.basepro.feature.places.ui.CoffeeShopUIRoute

fun NavGraphBuilder.healthNavGraph(navController: NavHostController, paddingVals: PaddingValues) {
    navigation(
        startDestination = Screen.PlacesScreen.route,
        route = HEALTH
    ) {
        composable(
            Screen.PlacesScreen.route
        ) {
            HealthRoute(navController = navController, paddingValues = paddingVals)
        }
    }
}