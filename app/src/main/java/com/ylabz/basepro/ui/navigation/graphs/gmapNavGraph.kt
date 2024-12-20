package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.MAP
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.maps.ui.MapUIRoute
import com.ylabz.basepro.ui.bar.AppScaffold
import kotlinx.coroutines.CoroutineScope


fun NavGraphBuilder.gmapNavGraph(
    drawerState: DrawerState, navController: NavHostController,scope: CoroutineScope
) {
    navigation(
        startDestination = Screen.MapScreen.route,
        route = MAP
    ) {
        composable(
            Screen.MapScreen.route
        ) {
            AppScaffold(
                route.toString(),
                scope = scope,
                drawerState = drawerState,
                navController = navController
            ) { innerPadding ->
                MapUIRoute(paddingValues = innerPadding,
                    navTo = { path -> navController.navigate(path) },
                )
            }
        }
    }
}

/*
AppScaffold(
                route.toString(),
                drawerState = drawerState,
                scope = scope,
                navController = navController
            ) { innerPadding ->
                 MapUIRoute(paddingValues = paddingVals,
                navTo = { path -> navController.navigate(path) },
            )
            }
 */