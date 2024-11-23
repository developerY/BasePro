package com.ylabz.basepro.feature.heatlh.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun HealthStartScreen(
    viewModel: HealthViewModel = hiltViewModel(),
    navController: NavController,
    //scaffoldState: ScaffoldState,
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val permissionsGranted by viewModel.permissionsGranted
    val sessionsList by viewModel.sessionsList
    val permissions = viewModel.permissions
    val backgroundReadPermissions = viewModel.backgroundReadPermissions
    val backgroundReadAvailable by viewModel.backgroundReadAvailable
    val backgroundReadGranted by viewModel.backgroundReadGranted
    val onPermissionsResult = { viewModel.initialLoad() }
    val permissionsLauncher =
        rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
            onPermissionsResult()
        }

  HealthPermissionScreen(
        permissionsGranted = permissionsGranted,
        permissions = permissions,
        backgroundReadAvailable = backgroundReadAvailable,
        backgroundReadGranted = backgroundReadGranted,
        backgroundReadPermissions = backgroundReadPermissions,
        onReadClick = {
            //viewModel.enqueueReadStepWorker()
        },
        sessionsList = sessionsList,
        uiState = viewModel.uiState,
        onInsertClick = {
            //viewModel.insertExerciseSession()
        },
        onDetailsClick = { uid ->
            navController.navigate("exercise_session_detail/$uid")
        },
        onError = { exception ->
            //showExceptionSnackbar(scaffoldState, scope, exception)
        },
        onPermissionsResult = {
            viewModel.initialLoad()
        },
        onPermissionsLaunch = { values ->
            permissionsLauncher.launch(values)
        }
    )
}
