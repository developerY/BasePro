package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.PLACES
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.heatlh.ui.HealthRoute
import com.ylabz.basepro.feature.maps.ui.MapUIRoute
import com.ylabz.basepro.feature.places.ui.CoffeeShopUIRoute

fun NavGraphBuilder.placesNavGraph(navController: NavHostController, paddingVals: PaddingValues) {
    navigation(
        startDestination = Screen.PlacesScreen.route,
        route = PLACES
    ) {
        composable(
            Screen.PlacesScreen.route
        ) {
            CoffeeShopUIRoute(paddingValues = paddingVals,
                navTo = { path -> navController.navigate(path) })
        }
    }
}