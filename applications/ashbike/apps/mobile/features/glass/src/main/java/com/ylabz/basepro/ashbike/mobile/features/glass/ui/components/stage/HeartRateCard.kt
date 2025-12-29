package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.stage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.theme.GlassColors

@Composable
fun HeartRateCard(
    heartRate: String,
    modifier: Modifier = Modifier
) {
    // Parse Int safely for color logic
    val bpm = heartRate.toIntOrNull() ?: 0

    // Dynamic Color Logic
    val zoneColor = when {
        bpm > 160 -> GlassColors.ZoneMax      // Red
        bpm > 140 -> GlassColors.ZoneThreshold // Orange
        bpm > 120 -> GlassColors.ZoneAerobic   // Green
        else -> GlassColors.ZoneEasy          // Blue
    }

    Card(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "HEART RATE",
                style = MaterialTheme.typography.labelSmall,
                color = GlassColors.TextSecondary,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = zoneColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = heartRate,
                    fontSize = 32.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                // Small "BPM" unit
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "bpm",
                    fontSize = 12.sp,
                    color = GlassColors.TextSecondary,
                    modifier = Modifier.align(Alignment.Bottom)
                )
            }
        }
    }
}