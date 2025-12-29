package com.ylabz.basepro.ashbike.mobile.features.glass.ui.panels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.common.GlassListItem
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.common.ListDividerScroll

@Composable
fun RideStatScrolList(
    distance: String,
    duration: String,
    avgSpeed: String,
    calories: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        // LAZY COLUMN: Enables scrolling
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp), // Slight padding for focus ring
            verticalArrangement = Arrangement.SpaceEvenly // Distribute nicely
        ) {
            item {
                GlassListItem(
                    Icons.Default.Straighten,
                    "DISTANCE",
                    "$distance km",
                    GlimmerTheme.colors.secondary
                )
                ListDividerScroll()
            }
            item {
                GlassListItem(
                    Icons.Default.AvTimer,
                    "DURATION",
                    duration,
                    GlimmerTheme.colors.surface
                )
                ListDividerScroll()
            }
            item {
                GlassListItem(
                    Icons.Default.Speed,
                    "AVG SPEED",
                    "$avgSpeed mph",
                    GlimmerTheme.colors.surface
                )
                ListDividerScroll()
            }
            item {
                GlassListItem(
                    Icons.Default.LocalFireDepartment,
                    "CALORIES",
                    calories,
                    GlimmerTheme.colors.negative // Or custom Orange if strictly needed
                )
                // No divider on the last item usually
            }
        }
    }
}

