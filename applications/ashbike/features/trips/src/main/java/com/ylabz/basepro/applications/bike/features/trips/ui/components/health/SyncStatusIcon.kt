package com.ylabz.basepro.applications.bike.features.trips.ui.components.health

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncDisabled
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class SyncHealthConnectState {
    HEALTH_UNAVAILABLE,     // never enabled
    NO_RECORDS_ADDED,       // enabled but nothing written
    ALL_SYNCED              // has at least one session in GHC
}


@Composable
fun SyncStatusIcon(state: SyncHealthConnectState, modifier: Modifier = Modifier) {
    val (icon, tint, description) = when (state) {
        SyncHealthConnectState.HEALTH_UNAVAILABLE -> Triple(
            Icons.Default.SyncDisabled,
            MaterialTheme.colorScheme.error,
            "Health Connect off"
        )
        SyncHealthConnectState.NO_RECORDS_ADDED -> Triple(
            Icons.Outlined.SyncProblem,
            MaterialTheme.colorScheme.secondary,
            "No sessions synced"
        )
        SyncHealthConnectState.ALL_SYNCED -> Triple(
            Icons.Default.Sync,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "All sessions synced"
        )
    }

    Icon(
        imageVector = icon,
        contentDescription = description,
        tint = tint,
        modifier = modifier.size(24.dp)
    )
}
