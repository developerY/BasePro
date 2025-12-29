package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.status

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.theme.GlassColors

@Composable
fun BikeConnectionStatus(
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isConnected) GlassColors.NeonGreen else GlassColors.WarningRed
    val icon = if (isConnected) Icons.AutoMirrored.Filled.DirectionsBike else Icons.Default.LinkOff
    val text = if (isConnected) "BLE CNX" else "NO BLE CNX"

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cool Bike Icon
        Icon(
            imageVector = icon,
            contentDescription = "Bike Connection",
            tint = color,
            modifier = Modifier.width(18.dp) // Slightly larger than text
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Status Text (Optional: could hide text to save space)
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}