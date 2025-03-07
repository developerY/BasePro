package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                label = stat.label,
                value = stat.value,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}