package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ylabz.basepro.core.ui.Screen
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.NAV3
import com.ylabz.basepro.core.ui.WEATHER
import com.ylabz.basepro.feature.nav3.ui.Nav3Main
import com.ylabz.basepro.ui.bar.AppScaffold
import kotlinx.coroutines.CoroutineScope


fun NavGraphBuilder.nav3NavGraph(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.Nav3Screen.route,
        route = NAV3
    ) {
        composable(
            Screen.Nav3Screen.route
        ) {
            AppScaffold(
                route.toString(),
                scope = scope,
                drawerState = drawerState,
                navController = navController
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Nav3Main(modifier = Modifier)
                }
            }
        }
    }
}