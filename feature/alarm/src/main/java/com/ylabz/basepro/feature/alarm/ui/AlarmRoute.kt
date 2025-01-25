package com.ylabz.basepro.feature.alarm.ui

import android.R.attr.data
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ylabz.basepro.core.model.alarm.ProAlarm
import com.ylabz.basepro.feature.alarm.ui.components.AlarmSuccessScreen
import com.ylabz.basepro.feature.alarm.ui.components.ErrorScreen
import com.ylabz.basepro.feature.alarm.ui.components.LoadingScreen
import kotlin.random.Random

@SuppressLint("MissingPermission")
@Composable
fun AlarmRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: AlarmViewModel = hiltViewModel()
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
            is AlarmUiState.Loading -> LoadingScreen()
            is AlarmUiState.Success -> AlarmSuccessScreen(
                data = uiState.data,
                onAddAlarmClick = {
                    val currentTime = System.currentTimeMillis() + 1000
                    val id = Random.nextInt()
                    val proAlarm = ProAlarm(
                        id = id,
                        timeInMillis = currentTime,
                        message = "Test Alarm $id"
                    )
                    viewModel.onEvent(AlarmEvent.AddAlarm(proAlarm))
                },
                onDeleteAllClick = {
                    viewModel.onEvent(AlarmEvent.DeleteAll)
                },
                onToggleAlarm = { alarmId, isEnabled ->
                    viewModel.onEvent(AlarmEvent.ToggleAlarm(alarmId, isEnabled))
                }
            )

            is AlarmUiState.Error -> ErrorScreen(uiState.message)
            AlarmUiState.Empty -> AlarmSuccessScreen(
            data = emptyList(),
            onAddAlarmClick = {
                val currentTime = System.currentTimeMillis() + 1000
                val id = Random.nextInt()
                val proAlarm = ProAlarm(
                    id = id,
                    timeInMillis = currentTime,
                    message = "Test Alarm $id"
                )
                viewModel.onEvent(AlarmEvent.AddAlarm(proAlarm))
            },
            onDeleteAllClick = {
                viewModel.onEvent(AlarmEvent.DeleteAll)
            },
            onToggleAlarm = { alarmId, isEnabled ->
                viewModel.onEvent(AlarmEvent.ToggleAlarm(alarmId, isEnabled))
            }
        )

        }
    }
}

// Ex.
// https://github.com/santansarah/ble-scanner/blob/main/app/src/main/java/com/santansarah/scan/presentation/scan/Home.kt
