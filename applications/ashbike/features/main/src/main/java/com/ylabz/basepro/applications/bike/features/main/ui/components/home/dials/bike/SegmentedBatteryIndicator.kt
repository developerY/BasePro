package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp

// Example 10-segment indicator
@Composable
fun SegmentedBatteryIndicator(batteryLevel: Int) {
    val segments = 10
    val filledSegments = (batteryLevel / (100 / segments)).coerceAtMost(10)
    // Color logic (red -> green) or discrete steps
    val fillColor = batteryColor(batteryLevel)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(segments) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = if (index < filledSegments) fillColor else Color.LightGray,
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
    }
}

// Simple color function from red to green
fun batteryColor(batteryLevel: Int): Color {
    val clamped = batteryLevel.coerceIn(0, 100)
    val fraction = clamped / 100f
    return lerp(Color.Red, Color.Green, fraction)
}