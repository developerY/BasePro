package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text

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
            verticalArrangement = Arrangement.spacedBy(4.dp) // Space between items
        ) {
            item {
                GlassListItem(Icons.Default.Straighten, "DISTANCE", "$distance km", GlassColors.NeonCyan)
                ListDivider()
            }
            item {
                GlassListItem(Icons.Default.AvTimer, "DURATION", duration, Color.White)
                ListDivider()
            }
            item {
                GlassListItem(Icons.Default.Speed, "AVG SPEED", "$avgSpeed mph", Color.White)
                ListDivider()
            }
            item {
                GlassListItem(Icons.Default.LocalFireDepartment, "CALORIES", calories, Color(0xFFFF9800))
                // No divider on the last item usually
            }
        }
    }
}

@Composable
private fun GlassListItem(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    // STATE: Track focus to highlight the row when selected (enables scrolling via D-pad)
    var isFocused by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp) // Taller touch/focus target for easier scrolling
            .onFocusChanged { isFocused = it.isFocused }
            .focusable() // <--- CRITICAL: Makes it selectable so the list scrolls
            .background(
                color = if (isFocused) Color.White.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp)
    ) {
        // LEADING ICON
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // TEXT CONTENT
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
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

@Composable
private fun ListDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 48.dp, end = 8.dp),
        thickness = 1.dp,
        color = Color.White.copy(alpha = 0.1f)
    )
}