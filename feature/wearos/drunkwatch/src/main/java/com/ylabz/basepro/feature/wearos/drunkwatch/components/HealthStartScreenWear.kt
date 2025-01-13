package com.ylabz.basepro.feature.wearos.drunkwatch.components

import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.items
import androidx.wear.compose.material.Text
import com.ylabz.basepro.feature.wearos.health.ui.HealthEvent

@Composable
fun HealthStartScreenWear(
    navController: NavController,
    healthData: List<ExerciseSessionRecord>,
    onEvent: (HealthEvent) -> Unit,
    onRequestPermissions: (Array<String>) -> Unit
) {
    ScalingLazyColumn {
        item {
            Text(text = "Wear Health Data")
        }
        items(healthData.size) { dataItem ->
            // Display each dataItem; for example:
            Text("• $dataItem")
        }
        item {
            // Example button or clickable text
            TextButton(
                onClick = { /*onEvent(HealthEvent.StartSession)*/ }
            ) {
                Text(text = "Start Session")
            }
        }
    }
}
