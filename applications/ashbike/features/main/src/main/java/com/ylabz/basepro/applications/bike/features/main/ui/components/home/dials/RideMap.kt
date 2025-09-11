package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.core.ui.NavigationCommand

@Composable
fun RideMap(
    modifier: Modifier = Modifier,
    uiState: BikeUiState.Success, // Kept for potential future use or consistency
    onEvent: (BikeEvent) -> Unit,  // Kept for potential future use or consistency
    navTo: (NavigationCommand) -> Unit, // Kept for potential future use or consistency
    onMapIconClick: () -> Unit      // New callback for map icon click
) {
    Box(
        modifier = modifier.clickable {
            onMapIconClick() // Invoke the new callback
        },
        contentAlignment = Alignment.Center
    ) {
        // The container size seems to be 32.dp based on 24.dp icon + 8.dp padding/spacing logic elsewhere.
        // Let's ensure the clickable area is reasonably sized for the icon.
        Box(
            modifier = Modifier
                .size(32.dp), // Explicitly size the clickable area if needed, or rely on parent padding
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = "Show Map",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
