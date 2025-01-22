package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.ALARM
import com.ylabz.basepro.core.ui.HEALTH
import com.ylabz.basepro.core.ui.SHOTIME
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.alarm.ui.AlarmRoute
import com.ylabz.basepro.feature.heatlh.ui.HealthRoute
import com.ylabz.basepro.feature.places.ui.CoffeeShopUIRoute
import com.ylabz.basepro.feature.shotime.ui.ShotimeRoute
import com.ylabz.basepro.ui.bar.AppScaffold
import kotlinx.coroutines.CoroutineScope

fun NavGraphBuilder.alarmNavGraph(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.AlarmScreen.route,
        route = ALARM
    ) {
        composable(
            Screen.AlarmScreen.route
        ) {
            AppScaffold(
                route.toString(),
                scope = scope,
                drawerState = drawerState,
                navController = navController
            ) { paddingVals ->


                AlarmRoute(navController = navController, paddingValues = paddingVals)
            }
        }
    }
}