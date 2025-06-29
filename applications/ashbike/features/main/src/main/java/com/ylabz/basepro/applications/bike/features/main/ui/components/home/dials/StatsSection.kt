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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straight
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatItem
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatCard
import com.ylabz.basepro.core.ui.theme.iconColorCalories
import com.ylabz.basepro.core.ui.theme.iconColorSpeed

@Composable
fun StatsSection(
    stats: List<StatItem>,
    modifier: Modifier = Modifier,
    contentColor: Color // Default content color when not active
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        stats.forEach { stat ->
            StatCard(
                icon = stat.icon,
                // Use activeColor if available, otherwise default contentColor
                tint = stat.activeColor ?: contentColor,
                label = stat.label,
                value = stat.value,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}

@Preview
@Composable
fun StatsSectionPreviewOn() {
    val stats = listOf(
        StatItem(
            icon = Icons.Filled.Favorite,
            label = "Heart Rate",
            value = "120 bpm",
            activeColor = MaterialTheme.colorScheme.iconColorSpeed // Example active color
        ),
        StatItem(
            icon = Icons.Filled.LocalFireDepartment,
            label = "Calories",
            value = "300 kcal",
            activeColor = MaterialTheme.colorScheme.iconColorCalories // Example active color
        )
    )
    StatsSection(
        stats = stats,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
}

@Preview
@Composable
fun StatsSectionPreviewOff() {
    val stats = listOf(
        StatItem(
            icon = Icons.Filled.Favorite,
            label = "Heart Rate",
            value = "-- bpm"
        ),
        StatItem(
            icon = Icons.Filled.LocalFireDepartment,
            label = "Calories",
            value = "-- kcal"
        )
    )
    StatsSection(
        stats = stats,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant // Or a more muted color for off state
    )
}
