package com.ylabz.basepro.ashbike.wear.presentation.screens.health

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import kotlin.math.max

@Composable
fun HeartRateZoneChart(
    heartRates: List<Int>,
    maxHeartRate: Int = 190, // Default Max HR (approx for 30yo)
    modifier: Modifier = Modifier
) {
    if (heartRates.isEmpty()) return

    // 1. Calculate Zones
    // Zones: 1 (Grey), 2 (Blue), 3 (Green), 4 (Orange), 5 (Red)
    val zones = remember(heartRates) {
        val counts = IntArray(5)
        heartRates.forEach { hr ->
            val percentage = hr.toFloat() / maxHeartRate
            val zoneIndex = when {
                percentage < 0.60 -> 0 // Zone 1 (Warm Up)
                percentage < 0.70 -> 1 // Zone 2 (Fat Burn)
                percentage < 0.80 -> 2 // Zone 3 (Aerobic)
                percentage < 0.90 -> 3 // Zone 4 (Cardio)
                else -> 4              // Zone 5 (Peak)
            }
            counts[zoneIndex]++
        }
        counts
    }

    // 2. Visual Configuration
    val zoneColors = listOf(
        Color(0xFF9E9E9E), // Zone 1: Grey
        Color(0xFF42A5F5), // Zone 2: Blue
        Color(0xFF66BB6A), // Zone 3: Green
        Color(0xFFFFA726), // Zone 4: Orange
        Color(0xFFEF5350)  // Zone 5: Red
    )

    val maxCount = zones.maxOrNull()?.coerceAtLeast(1) ?: 1

    Column(modifier = modifier.fillMaxWidth()) {
        // Label
        Text(
            text = "Time in Zones",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Graph
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            val barWidth = size.width / 9f // 5 bars + spaces
            val spacing = barWidth / 2f
            val maxBarHeight = size.height

            zones.forEachIndexed { index, count ->
                val barHeight = (count.toFloat() / maxCount) * maxBarHeight
                val x = index * (barWidth + spacing) + spacing // Center it roughly

                // Draw Bar
                drawRoundRect(
                    color = zoneColors[index],
                    topLeft = Offset(x, maxBarHeight - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
        }

        // Legend (Simplified)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Low", style = MaterialTheme.typography.bodyExtraSmall, color = Color.Gray)
            Text("High", style = MaterialTheme.typography.bodyExtraSmall, color = Color.Gray)
        }
    }
}