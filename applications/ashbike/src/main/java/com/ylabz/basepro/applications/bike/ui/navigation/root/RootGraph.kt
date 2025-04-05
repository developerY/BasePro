package com.ylabz.basepro.applications.bike.ui.navigation.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ylabz.basepro.applications.bike.ui.BikeUiRoute
import com.ylabz.basepro.core.ui.BIKE
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.core.ui.ROOT
import com.ylabz.basepro.settings.ui.SettingsUiRoute


@Composable
fun RootNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    startDestination: String = BikeScreen.HomeBikeScreen.route
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        modifier = modifier,
        route = ROOT,
    ) {

        composable(BikeScreen.HomeBikeScreen.route) {
            BikeUiRoute(
                modifier = modifier,
                navTo = { path -> navHostController.navigate(path) }
            )
        }

        composable(BikeScreen.TripBikeScreen.route) {
            BikeUiRoute(
                modifier = modifier,
                navTo = { path -> navHostController.navigate(path)
            })
        }

        composable(BikeScreen.SettingsBikeScreen.route) {
            SettingsUiRoute(
                modifier = modifier,
                navTo = { path -> navHostController.navigate(path) }
            )
        }
    }

}

/*
composable(route = MAIN) {
            MainScreen(navHostController)
        }
 */