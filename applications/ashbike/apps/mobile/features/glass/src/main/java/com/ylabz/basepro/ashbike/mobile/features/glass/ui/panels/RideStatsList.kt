package com.ylabz.basepro.ashbike.mobile.features.glass.ui.panels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.HorizontalDivider
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
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.theme.GlassColors

@Composable
fun RideStatsList(
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
                .padding(horizontal = 12.dp, vertical = 4.dp), // Tight padding for density
            verticalArrangement = Arrangement.SpaceAround // Distribute evenly
        ) {
            // 1. Distance
            GlassListItem(
                icon = Icons.Default.Straighten,
                label = "DISTANCE",
                value = "$distance km",
                iconTint = GlassColors.NeonCyan
            )

            HorizontalDivider()

            // 2. Duration
            GlassListItem(
                icon = Icons.Default.AvTimer,
                label = "DURATION",
                value = duration,
                iconTint = Color.White
            )

            HorizontalDivider()

            // 3. Avg Speed
            GlassListItem(
                icon = Icons.Default.Speed,
                label = "AVG SPEED",
                value = "$avgSpeed mph",
                iconTint = Color.White
            )

            HorizontalDivider()

            // 4. Calories
            GlassListItem(
                icon = Icons.Default.LocalFireDepartment,
                label = "CALORIES",
                value = calories,
                iconTint = Color(0xFFFF9800) // Orange
            )
        }
    }
}

/**
 * A specialized List Item for the Glass HUD.
 * Follows the pattern: [Icon]  [Big Value]
 * [Small Label]
 */
@Composable
private fun GlassListItem(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(36.dp) // Fixed height for consistency
    ) {
        // LEADING ICON
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // TEXT CONTENT (Value Top, Label Bottom)
        Column(verticalArrangement = Arrangement.Center) {
            // HEADLINE: The Value (Big & White)
            Text(
                text = value,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1 // Prevent wrapping
            )

            // SUPPORTING: The Label (Small & Colored)
            Text(
                text = label,
                color = GlassColors.TextSecondary,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}