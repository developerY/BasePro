package com.ylabz.basepro.ashbike.mobile.features.glass.newui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AvTimer
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import com.ylabz.basepro.ashbike.mobile.features.glass.newui.elements.ListRow

@Composable
fun StatsBoard(
    distance: String,
    duration: String,
    avgSpeed: String,
    calories: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ListRow(
                    icon = Icons.Rounded.Straighten,
                    label = "DISTANCE",
                    value = "$distance km",
                    accent = GlimmerTheme.colors.secondary
                )
            }

            item {
                ListRow(
                    icon = Icons.Rounded.AvTimer,
                    label = "DURATION",
                    value = duration,
                    accent = GlimmerTheme.colors.secondary
                )
            }

            item {
                ListRow(
                    icon = Icons.Rounded.Speed,
                    label = "AVG SPEED",
                    value = "$avgSpeed mph",
                    accent = GlimmerTheme.colors.secondary
                )
            }

            item {
                ListRow(
                    icon = Icons.Rounded.LocalFireDepartment,
                    label = "CALORIES",
                    value = calories,
                    accent = GlimmerTheme.colors.negative
                )
            }
        }
    }
}