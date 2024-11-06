package com.ylabz.basepro.feature.heatlh.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HealthFeatureWithPermissions() {
    // Define the Health Connect permissions you want to request
    val healthPermissions = listOf(
        "android.permission.health.READ_HEART_RATE",
        "android.permission.health.WRITE_HEART_RATE",
        "android.permission.health.READ_STEPS",
        "android.permission.health.WRITE_STEPS"
    )

    // Remember multiple permission states for Health Connect permissions
    val healthPermissionStates = rememberMultiplePermissionsState(permissions = healthPermissions)

    // Check if all permissions are granted
    if (healthPermissionStates.allPermissionsGranted) {
        // Content shown when permissions are granted
        Text("Health permissions granted")
        // Here, you can call your Health Connect functions
    } else {
        // Rationale or request UI
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            val rationaleText = when {
                healthPermissionStates.shouldShowRationale -> {
                    // Show rationale when permissions have been denied before
                    "To access health data, please grant Health Connect permissions."
                }
                else -> {
                    // First-time request or if the user selected "Don't ask again"
                    "Health Connect permissions are required for this feature. Please grant them."
                }
            }

            Text(rationaleText)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { healthPermissionStates.launchMultiplePermissionRequest() }) {
                Text("Request Permissions")
            }
        }
    }
}
