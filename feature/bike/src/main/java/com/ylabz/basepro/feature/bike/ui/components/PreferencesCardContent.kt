package com.ylabz.basepro.feature.bike.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PreferencesCardContent(
    avoidHeavyTraffic: Boolean,
    onAvoidHeavyTrafficChange: (Boolean) -> Unit,
    preferFlatTerrain: Boolean,
    onPreferFlatTerrainChange: (Boolean) -> Unit,
    preferScenicRoutes: Boolean,
    onPreferScenicRoutesChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        PreferenceSwitch(
            label = "Avoid Heavy Traffic",
            checked = avoidHeavyTraffic,
            onCheckedChange = onAvoidHeavyTrafficChange,
            icon = Icons.Filled.DirectionsCar // e.g., DirectionsCar -- traffic-related
        )
        PreferenceSwitch(
            label = "Prefer Flat Terrain",
            checked = preferFlatTerrain,
            onCheckedChange = onPreferFlatTerrainChange,
            icon = Icons.Filled.Terrain // terrain icon
        )
        PreferenceSwitch(
            label = "Prefer Scenic Routes",
            checked = preferScenicRoutes,
            onCheckedChange = onPreferScenicRoutesChange,
            icon = Icons.Filled.Landscape // scenic icon
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
