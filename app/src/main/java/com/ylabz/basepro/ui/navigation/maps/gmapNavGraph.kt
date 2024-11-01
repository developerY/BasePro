package com.ylabz.basepro.ui.navigation.maps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.MAP
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.maps.ui.MapUIRoute


fun NavGraphBuilder.gmapNavGraph(navController: NavHostController, paddingVals: PaddingValues) {
    navigation(
        startDestination = Screen.MapScreen.route,
        route = MAP
    ) {
        composable(
            Screen.MapScreen.route
        ) {
            MapUIRoute(paddingValues = paddingVals,
                navTo = { path -> navController.navigate(path) },
            )
        }
    }
}