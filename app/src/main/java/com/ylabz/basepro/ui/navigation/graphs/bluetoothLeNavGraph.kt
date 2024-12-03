package com.ylabz.basepro.ui.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.core.ui.BLE
import com.ylabz.basepro.core.ui.MAP
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.ble.ui.BluetoothLeRoute
import com.ylabz.basepro.feature.ble.ui.components.PermissionsDenied
import com.ylabz.basepro.feature.maps.ui.MapUIRoute

fun NavGraphBuilder.bluetoothLeNavGraph(navController: NavHostController, paddingVals: PaddingValues) {
    navigation(
        startDestination = Screen.BLEScreen.route,
        route = BLE
    ) {
        composable(
            Screen.BLEScreen.route
        ) {
            BluetoothLeRoute(paddingValues = paddingVals,
                navTo = { path -> navController.navigate(path) },
            )
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