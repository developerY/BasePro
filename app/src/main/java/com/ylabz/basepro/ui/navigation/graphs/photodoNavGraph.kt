package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.applications.photodo.ui.PhotodoRoute
import com.ylabz.basepro.core.ui.BIKE
import com.ylabz.basepro.core.ui.PHOTODO
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.ui.bar.AppScaffold
import kotlinx.coroutines.CoroutineScope

fun NavGraphBuilder.photodoNavGraph(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.PhotodoScreen.route,
        route = PHOTODO
    ) {
        composable(
            Screen.PhotodoScreen.route
        ) {
            AppScaffold(
                route.toString(),
                scope = scope,
                drawerState = drawerState,
                navController = navController
            ) { padding ->
                PhotodoRoute(
                    modifier = Modifier.padding(padding),
                    navTo = { path -> navController.navigate(path) }
                )
            }
        }
    }
}