package com.ylabz.basepro.applications.bike.ui.navigation.root

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ylabz.basepro.applications.bike.ui.BikeUiRoute
import com.ylabz.basepro.applications.bike.ui.navigation.main.MainScreen
import com.ylabz.basepro.core.ui.BIKE
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.core.ui.ROOT


@Composable
fun RootNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    //padding:PaddingValues,
    startDestination: String = BIKE
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        route = ROOT,
    ) {

        composable("bike") {
            BikeUiRoute(navTo = { path -> navHostController.navigate(path) }
            )

            composable("trip_bike_screen") { BikeUiRoute(navTo = { path ->
                navHostController.navigate(path)
            })}

            composable("settings_bike_screen") {
                    BikeUiRoute(navTo = { path -> navHostController.navigate(path) })
                }
            }
    }
}

/*
composable(route = MAIN) {
            MainScreen(navHostController)
        }
 */