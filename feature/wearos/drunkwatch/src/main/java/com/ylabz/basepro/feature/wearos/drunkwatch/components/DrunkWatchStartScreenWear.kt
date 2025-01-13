package com.ylabz.basepro.feature.wearos.drunkwatch.components

import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.ylabz.basepro.feature.wearos.drunkwatch.DrukWatchEvent

@Composable
fun DrunkWatchStartScreenWear(
    navController: NavController,
    onEvent: (DrukWatchEvent) -> Unit,
    onRequestPermissions: (Array<String>) -> Unit
) {
    Text("Drunk Watch")
    ScalingLazyColumn {
        item {
            Text(text = "Wear Drunk Watch")
        }
        item {
            // Example button or clickable text
            TextButton(
                onClick = { /*onEvent(HealthEvent.StartSession)*/ }
            ) {
                Text(text = "Drunk Watch")
            }
        }
    }
}
