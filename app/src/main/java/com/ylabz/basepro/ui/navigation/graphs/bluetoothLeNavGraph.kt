package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.material3.DrawerState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.BLE
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.ble.ui.BluetoothLeRoute
import com.ylabz.basepro.ui.bar.AppScaffold
import kotlinx.coroutines.CoroutineScope

fun NavGraphBuilder.bluetoothLeNavGraph(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.BLEScreen.route,
        route = BLE
    ) {
        composable(
            Screen.BLEScreen.route
        ) {
            AppScaffold(
                route.toString(),
                scope = scope,
                drawerState = drawerState,
                navController = navController
            ) { innerPadding ->
                BluetoothLeRoute(
                    paddingValues = innerPadding,
                    // navTo = { path -> navController.navigate(path) },
                )
            }
        }

        /*composable(
            Screen.BLEPermissionsScreen.route
        ) {
            PermissionsDenied {
                permissionState.launchMultiplePermissionRequest() // Trigger permission request
                // viewModel.onEvent(BluetoothLeEvent.PermissionsDenied)
            }
        }*/
    }
}