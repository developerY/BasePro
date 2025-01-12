package com.ylabz.basepro.feature.wearos.health.ui.components

import android.R.attr.onClick
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text

@Composable
fun HealthFeatureWithPermissionsWear(onRequestPermissions: () -> Unit) {
    // You might use a full-screen Box or a ScalingLazyColumn for a watch UI
    TextButton(
        onClick = onRequestPermissions,
        modifier = Modifier
    ) {
        Text("Grant Health Permissions")
    }
}
