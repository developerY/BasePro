package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.core.ui.NavigationCommand

@Composable
fun RideMap(
    modifier: Modifier = Modifier,
    uiState: BikeUiState.Success,
    onEvent: (BikeEvent) -> Unit, // Modified signature
    navTo: (NavigationCommand) -> Unit, // MODIFIED: Changed from onEvent to navTomodifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clickable {
            // onEvent(BikeEvent.ShowMap())
        },
        contentAlignment = Alignment.Center
    ) {
        val indicatorContainerSize = 24.dp + 8.dp
        Box(
            modifier = Modifier
                .size(indicatorContainerSize),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = "Show Map",
                // tint = Color.Yellow, //animatedColor.value,
                modifier = Modifier.size(24.dp)
            )
        }
    }
    
}