package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.NFC
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.nfc.ui.NfcUiRoute
import com.ylabz.basepro.ui.bar.AppScaffold
import kotlinx.coroutines.CoroutineScope

fun NavGraphBuilder.nfcNavGraph(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.NfcScreen.route,
        route = NFC
    ) {
        composable(
            Screen.NfcScreen.route
        ) {
            AppScaffold(
                route.toString(),
                scope = scope,
                drawerState = drawerState,
                navController = navController
            ) { paddingVals ->
                NfcUiRoute(
                    modifier = Modifier.padding(paddingVals),
                    navTo = { path -> navController.navigate(path) }
                )
            }
        }
    }
}