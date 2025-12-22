package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text

@Composable
fun RideStatsStack(
    distance: String,
    duration: String,
    avgSpeed: String,
    calories: String,
    // We still pass the requester to focus the first card
    stackFocusRequester: FocusRequester? = null,
    modifier: Modifier = Modifier
) {
    // A Vertical Stack of Cards
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp), // Gap between cards
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 8.dp)
    ) {
        // CARD 1: Distance
        item {
            StatCard(
                icon = Icons.Default.Straighten,
                label = "DISTANCE",
                value = "$distance km",
                iconTint = GlassColors.NeonCyan,
                // Attach focus requester to the first card
                modifier = if (stackFocusRequester != null) Modifier.focusRequester(stackFocusRequester) else Modifier
            )
        }

        // CARD 2: Duration
        item {
            StatCard(
                icon = Icons.Default.AvTimer,
                label = "DURATION",
                value = duration,
                iconTint = Color.White
            )
        }

        // CARD 3: Avg Speed
        item {
            StatCard(
                icon = Icons.Default.Speed,
                label = "AVG SPEED",
                value = "$avgSpeed mph",
                iconTint = Color.White
            )
        }

        // CARD 4: Calories
        item {
            StatCard(
                icon = Icons.Default.LocalFireDepartment,
                label = "CALORIES",
                value = calories,
                iconTint = Color(0xFFFF9800) // Orange
            )
        }
    }
}

/**
 * A single Card in the Stack.
 * Glimmer Cards are naturally focusable, so this works out of the box.
 */
@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    // We use a Glimmer Card for each item
    Card(
        modifier = modifier.fillMaxWidth(),
        // onClick is required to make the card interactive/focusable on some XR devices
        onClick = { /* No-op, just for focus */ }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp) // Internal padding
        ) {
            // ICON
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // TEXT STACK
            Column {
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    color = GlassColors.TextSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}