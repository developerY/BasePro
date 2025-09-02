package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.material3.DrawerState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.PLACES
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.places.ui.CoffeeShopUIRoute
import com.ylabz.basepro.ui.bar.AppScaffold
import kotlinx.coroutines.CoroutineScope

fun NavGraphBuilder.placesNavGraph(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.PlacesScreen.route,
        route = PLACES
    ) {
        composable(
            Screen.PlacesScreen.route
        ) {
            AppScaffold(
                route.toString(),
                drawerState = drawerState,
                scope = scope,
                navController = navController
            ) { paddingVals ->
                CoffeeShopUIRoute(
                    paddingValues = paddingVals,
                    navTo = { path -> navController.navigate(path) })
            }
        }
    }
}