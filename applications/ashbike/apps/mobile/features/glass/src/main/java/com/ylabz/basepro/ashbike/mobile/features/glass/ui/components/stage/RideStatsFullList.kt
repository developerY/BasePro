package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.stage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.GlassColors

@Composable
fun RideStatsFullList(
    distance: String,
    duration: String,
    avgSpeed: String,
    calories: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceEvenly // Distribute nicely
        ) {
            // 1. Distance
            StatRow(
                icon = Icons.Default.Straighten,
                label = "DIST",
                value = "$distance km",
                color = GlassColors.NeonCyan
            )

            // 2. Duration
            StatRow(
                icon = Icons.Default.AvTimer,
                label = "TIME",
                value = duration,
                color = Color.White
            )

            // 3. Avg Speed
            StatRow(
                icon = Icons.Default.Speed,
                label = "AVG",
                value = "$avgSpeed mph",
                color = Color.White
            )

            // 4. Calories
            StatRow(
                icon = Icons.Default.LocalFireDepartment,
                label = "CAL",
                value = "$calories",
                color = Color(0xFFFF9800) // Orange
            )
        }
    }
}

// Helper Composable for consistent rows
@Composable
private fun StatRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.width(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Label (Small, Gray)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = GlassColors.TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(36.dp) // Fixed width aligns the values
        )

        // Value (Bold)
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}