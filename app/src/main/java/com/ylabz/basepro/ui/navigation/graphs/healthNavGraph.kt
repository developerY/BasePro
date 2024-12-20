package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.HEALTH
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.heatlh.ui.HealthRoute
import com.ylabz.basepro.feature.places.ui.CoffeeShopUIRoute
import com.ylabz.basepro.ui.bar.AppScaffold
import kotlinx.coroutines.CoroutineScope

fun NavGraphBuilder.healthNavGraph(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.PlacesScreen.route,
        route = HEALTH
    ) {
        composable(
            Screen.PlacesScreen.route
        ) {
            AppScaffold(
                route.toString(),
                scope = scope,
                drawerState = drawerState,
                navController = navController
            ) { paddingVals ->


                HealthRoute(navController = navController, paddingValues = paddingVals)
            }
        }
    }
}