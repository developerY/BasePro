package com.ylabz.basepro.applications.photodo.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ylabz.basepro.applications.photodo.ui.components.LoadingScreen
import com.ylabz.basepro.applications.photodo.ui.components.ErrorScreen
import com.ylabz.basepro.applications.photodo.ui.components.PhotodoScreen

@SuppressLint("MissingPermission")
@Composable
fun PhotodoRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    //navController: NavController,
    viewModel: PhotodoViewModel = hiltViewModel()
) {
    val TAG = "PhotodoRoute"
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    // Render the UI based on the current state
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        //PermissionStatusUI(permissionState) // Show BLE permission status visually
        // Status Bar at the top of the screen

        when (uiState) {
            is PhotodoUiState.Loading -> LoadingScreen()
            is PhotodoUiState.Success -> PhotodoScreen(
                data = uiState.data,
                setAlarm = { alarm ->
                    viewModel.setAlarm(alarm)
                }
            )
            is PhotodoUiState.Error -> ErrorScreen(uiState.message)
        }
    }
}

// Ex.
// https://github.com/santansarah/ble-scanner/blob/main/app/src/main/java/com/santansarah/scan/presentation/scan/Home.kt
