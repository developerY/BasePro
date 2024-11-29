package com.ylabz.basepro.feature.heatlh.ui.components

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
import java.util.UUID

@Composable
fun HealthPermissionScreen(
    isHealthConnectAvailable: Boolean,
    permissions: Set<String>,
    permissionsGranted: Boolean,
    backgroundReadPermissions: Set<String>,
    backgroundReadAvailable: Boolean,
    backgroundReadGranted: Boolean,
    onReadClick: () -> Unit = {},
    sessionsList: List<ExerciseSessionRecord>,
    uiState: HealthUiState,
    onInsertClick: () -> Unit = {},
    onDetailsClick: (String) -> Unit = {},
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    onPermissionsLaunch: (Set<String>) -> Unit = {},
) {
    val activity = LocalContext.current as? ComponentActivity

    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }

    LaunchedEffect(uiState) {
        if (uiState is HealthUiState.Uninitialized) {
            onPermissionsResult()
        }

        /*if (uiState is HealthUiState.Error.Exception && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }*/
    }

    if (uiState != HealthUiState.Uninitialized) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = if (isHealthConnectAvailable) {
                        "Health Connect is available"
                    } else {
                        "Health Connect is not available"
                    },
                    color = if (isHealthConnectAvailable) Color.Green else Color.Red,
                    fontWeight = if (isHealthConnectAvailable) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.LightGray)
                        .padding(8.dp), // Extra padding inside the background for better spacing
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            item {
                Button(
                    onClick = {
                        val settingsIntent = Intent()
                        settingsIntent.action = HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS
                        activity?.startActivity(settingsIntent)
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Open Health Connect Settings")
                }
            }
            if (!permissionsGranted) {
                item {
                    Button(
                        onClick = {
                            onPermissionsLaunch(permissions)
                        }
                    ) {
                        Text(text = "Request Permissions")
                    }
                }
            } else {
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(4.dp),
                        onClick = {
                            onInsertClick()
                        }
                    ) {
                        Text("Insert Exercise Session")
                    }
                }
                if (!backgroundReadGranted) {
                    item {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(4.dp),
                            onClick = {
                                onPermissionsLaunch(backgroundReadPermissions)
                            },
                            enabled = backgroundReadAvailable,
                        ) {
                            if (backgroundReadAvailable) {
                                Text("Request Background Read")
                            } else {
                                Text("Background Read Not Available")
                            }
                        }
                    }
                } else {
                    item {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(4.dp),
                            onClick = {
                                onReadClick()
                            },
                        ) {
                            Text("Read Steps in Background")
                        }
                    }
                }
            }
        }
    }
}