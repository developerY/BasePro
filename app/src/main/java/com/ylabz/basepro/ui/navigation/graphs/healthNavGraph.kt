package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.HEALTH
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.heatlh.ui.HealthRoute
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
import com.ylabz.basepro.feature.heatlh.ui.components.SessionDetailScreen
import com.ylabz.basepro.feature.places.ui.CoffeeShopUIRoute
import com.ylabz.basepro.ui.bar.AppScaffold
import kotlinx.coroutines.CoroutineScope

fun NavGraphBuilder.healthNavGraph(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.HealthScreen.route,
        route = HEALTH
    ) {
        // —————————————————————————————————————————————————————————
        //  1) Your “list” screen
        // —————————————————————————————————————————————————————————
        composable(Screen.HealthScreen.route) {
            AppScaffold(
                route.toString(),
                scope = scope,
                drawerState = drawerState,
                navController = navController
            ) { paddingVals ->
                HealthRoute(
                    modifier = Modifier.padding(paddingVals),
                    //navTo = { path -> navController.navigate(path) }
                )
            }
        }

        // —————————————————————————————————————————————————————————
        //  2) Your new detail screen
        //     Matches navTo("exercise_session_detail/$uid")
        // —————————————————————————————————————————————————————————
        composable(
            route = "exercise_session_detail/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            // pull out the uid
            val uid = backStackEntry.arguments?.getString("uid")
                ?: return@composable
            val healthVM: HealthViewModel = hiltViewModel(backStackEntry)

            SessionDetailScreen(
                uid           = uid,
                navController = navController,
                modifier      = Modifier.fillMaxSize()
            )
        }
    }
}
