package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straight
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatItem
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatCard

@Composable
fun StatsSection(
    stats: List<StatItem>,
    modifier: Modifier = Modifier
) {
    // Display the given stats in a row, each as a card
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Distribute space among all stat items
        stats.forEach { stat ->
            StatCard(
                icon = stat.icon,
                tint = stat.tint,
                label = stat.label,
                value = stat.value,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}

@Preview
@Composable
fun StatsSectionPreview() {
    val stats = listOf(
        StatItem(
            icon = Icons.Filled.Straight,
            label = "Distance",
            value = "10.0 km"
        ),
        StatItem(
            icon = Icons.Filled.Speed,
            label = "Avg Speed",
            value = "25.0 km/h"
        )
    )
    StatsSection(stats = stats)
}