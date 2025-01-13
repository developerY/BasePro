package com.ylabz.basepro.feature.wearos.sleepwatch.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.ylabz.basepro.feature.wearos.sleepwatch.SleepWatchEvent

@Composable
fun SleepWatchStartScreenWear(
    navController: NavController,
    onEvent: (SleepWatchEvent) -> Unit,
    onRequestPermissions: (Array<String>) -> Unit
) {
    ScalingLazyColumn {
        item {
            Text(text = "Sleep Watch Health Data")
        }
    }
}
