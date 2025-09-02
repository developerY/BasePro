package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.SETTINGS
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.settings.ui.SettingsUiRoute
import com.ylabz.basepro.ui.bar.AppScaffold
import kotlinx.coroutines.CoroutineScope

fun NavGraphBuilder.settingsNavGraph(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.SettingsScreen.route,
        route = SETTINGS
    ) {
        composable(
            Screen.SettingsScreen.route
        ) {
            AppScaffold(
                route.toString(),
                drawerState = drawerState,
                scope = scope,
                navController = navController
            ) { padding ->
                SettingsUiRoute(
                    modifier = Modifier.padding(padding),
                    navTo = { path -> navController.navigate(path) }
                )
            }
        }
    }
}