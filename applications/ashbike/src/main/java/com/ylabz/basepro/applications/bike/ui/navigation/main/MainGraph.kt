package com.ylabz.basepro.applications.bike.ui.navigation.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ylabz.basepro.applications.bike.ui.BikeUiRoute
import com.ylabz.basepro.core.ui.BIKE
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.core.ui.MAIN
import com.ylabz.basepro.core.ui.Screen
import kotlinx.coroutines.CoroutineScope

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        route = BIKE,
        startDestination = BikeScreen.HomeBikeScreen.route
    ) {
        composable(
            BikeScreen.HomeBikeScreen.route
        ) {
            BikeUiRoute(
                navTo = { path -> navController.navigate(path) }
            )
        }
    }
}