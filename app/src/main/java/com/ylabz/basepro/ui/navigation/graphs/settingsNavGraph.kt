package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.PLACES
import com.ylabz.basepro.core.ui.SETTINGS
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.places.ui.CoffeeShopUIRoute
import com.ylabz.basepro.settings.ui.SettingsUiRoute

fun NavGraphBuilder.settingsNavGraph(navController: NavHostController, paddingVals: PaddingValues) {
    navigation(
        startDestination = Screen.SettingsScreen.route,
        route = SETTINGS
    ) {
        composable(
            Screen.SettingsScreen.route
        ) {
            SettingsUiRoute(paddingValues = paddingVals,
                navTo = { path -> navController.navigate(path) })
        }
    }
}