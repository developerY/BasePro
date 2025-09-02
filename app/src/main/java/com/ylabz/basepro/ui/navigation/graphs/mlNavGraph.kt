package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.ML
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.ml.ui.MLAppScreen
import com.ylabz.basepro.ui.bar.AppScaffold
import kotlinx.coroutines.CoroutineScope

fun NavGraphBuilder.mlNavGraph(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.MLScreen.route,
        route = ML
    ) {
        composable(
            Screen.MLScreen.route
        ) {
            AppScaffold(
                route.toString(),
                scope = scope,
                drawerState = drawerState,
                navController = navController
            ) { paddingVals ->
                MLAppScreen(
                    modifier = Modifier.padding(paddingVals)
                )
            }
        }
    }
}