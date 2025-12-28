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
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.BatteryZone // Import your new Enum

@Composable
fun BatteryStatusDisplay(
    zone: BatteryZone, // <--- Accepts the Enum, not the Int
    levelText: String, // <--- Accepts the pre-formatted text (e.g. "85%" or "--")
    modifier: Modifier = Modifier
) {
    // 1. Pure Visual Mapping (Style Only)
    val (icon, color) = when (zone) {
        BatteryZone.UNKNOWN -> Pair(Icons.AutoMirrored.Filled.BatteryUnknown, Color.Gray)
        BatteryZone.GOOD -> Pair(Icons.Default.BatteryFull, GlassColors.NeonGreen)
        BatteryZone.WARNING -> Pair(Icons.Default.BatteryStd, Color(0xFFFF9800))
        BatteryZone.CRITICAL -> Pair(Icons.Default.BatteryAlert, GlassColors.WarningRed)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Battery",
            tint = color,
            modifier = Modifier.width(16.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = levelText,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}