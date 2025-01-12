package com.ylabz.basepro.feature.wearos.health.ui.components

import androidx.compose.runtime.Composable
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Text

@Composable
fun ErrorScreenWear(message: String, onRetry: () -> Unit) {
    ScalingLazyColumn {
        item {
            Text(text = message)
        }
        item {
            androidx.wear.compose.material.Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
