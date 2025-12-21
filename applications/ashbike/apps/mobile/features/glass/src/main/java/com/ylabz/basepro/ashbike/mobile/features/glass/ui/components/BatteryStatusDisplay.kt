package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BatteryUnknown
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text

@Composable
fun BatteryStatusDisplay(
    level: Int?,
    modifier: Modifier = Modifier
) {
    // 1. Determine Color & Icon based on Level
    val (icon, color) = when {
        level == null -> Pair(Icons.AutoMirrored.Filled.BatteryUnknown, Color.Gray)
        level > 50 -> Pair(Icons.Default.BatteryFull, GlassColors.NeonGreen)
        level > 20 -> Pair(Icons.Default.BatteryStd, Color(0xFFFF9800)) // Orange
        else -> Pair(Icons.Default.BatteryAlert, GlassColors.WarningRed)
    }

    // 2. Render
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // The Icon
        Icon(
            imageVector = icon,
            contentDescription = "Battery",
            tint = color,
            // Glass icons often need to be slightly larger to be legible
            modifier = Modifier.width(16.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        // The Percentage Text
        Text(
            text = if (level != null) "$level%" else "--",
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}