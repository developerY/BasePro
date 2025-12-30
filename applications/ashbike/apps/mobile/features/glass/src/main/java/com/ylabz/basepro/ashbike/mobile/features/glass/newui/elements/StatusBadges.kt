package com.ylabz.basepro.ashbike.mobile.features.glass.newui.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.BatteryUnknown
import androidx.compose.material.icons.rounded.BatteryAlert
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.BatteryStd
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.LinkOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.BatteryZone

@Composable
fun BatteryBadge(
    zone: BatteryZone,
    level: String,
    modifier: Modifier = Modifier
) {
    val (icon, tint) = when (zone) {
        BatteryZone.UNKNOWN -> Icons.AutoMirrored.Rounded.BatteryUnknown to GlimmerTheme.colors.outline
        BatteryZone.CRITICAL -> Icons.Rounded.BatteryAlert to GlimmerTheme.colors.negative
        BatteryZone.WARNING -> Icons.Rounded.BatteryStd to GlimmerTheme.colors.positive
        BatteryZone.GOOD -> Icons.Rounded.BatteryFull to GlimmerTheme.colors.positive
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        // Icon(icon, null, tint = tint, modifier = Modifier.width(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(level, color = tint, style = GlimmerTheme.typography.bodySmall)
    }
}

@Composable
fun ConnectionBadge(active: Boolean, modifier: Modifier = Modifier) {
    val color = if (active) GlimmerTheme.colors.positive else GlimmerTheme.colors.negative
    val icon = if (active) Icons.Rounded.Link else Icons.Rounded.LinkOff
    val text = if (active) "BLE CNX" else "NO BLE"

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        // Icon(icon, null, tint = color, modifier = Modifier.width(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, color = color, style = GlimmerTheme.typography.bodySmall)
    }
}