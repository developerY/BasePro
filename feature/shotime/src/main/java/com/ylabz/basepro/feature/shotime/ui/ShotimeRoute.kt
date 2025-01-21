package com.ylabz.basepro.feature.shotime.ui

import android.Manifest
import android.R.attr.data
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ylabz.basepro.feature.shotime.ui.components.ErrorScreen
import com.ylabz.basepro.feature.shotime.ui.components.LoadingScreen
import com.ylabz.basepro.feature.shotime.ui.components.ShotimeSuccessScreen

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ShotimeRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: ShotimeViewModel = hiltViewModel()
) {
    val TAG = "ShotimeRoute"
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    // Render the UI based on the current state
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        //PermissionStatusUI(permissionState) // Show BLE permission status visually
        // Status Bar at the top of the screen

        when (uiState) {
            is ShotimeUiState.Loading -> LoadingScreen()
            is ShotimeUiState.Success -> ShotimeSuccessScreen(
                data = uiState.data,
                setAlarm = { alarm ->
                    viewModel.setAlarm(alarm)
                }
            )
            is ShotimeUiState.Error -> ErrorScreen(uiState.message)
        }
    }
}

// Ex.
// https://github.com/santansarah/ble-scanner/blob/main/app/src/main/java/com/santansarah/scan/presentation/scan/Home.kt
