package com.ylabz.basepro.ashbike.wear.presentation.screens.health

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text

@Composable
fun HeartRateSummaryCard(
    heartRates: List<Int>,
    maxHeartRate: Int = 190,
    modifier: Modifier = Modifier
) {
    if (heartRates.isEmpty()) return

    // --- SHARED CONFIG ---
    // Zone Colors: Grey, Blue, Green, Orange, Red
    val zoneColors = listOf(
        Color(0xFF9E9E9E), Color(0xFF42A5F5), Color(0xFF66BB6A), Color(0xFFFFA726), Color(0xFFEF5350)
    )

    // Calculate Data for Line Graph
    val minHr = remember(heartRates) { heartRates.minOrNull() ?: 0 }
    val maxHrData = remember(heartRates) { heartRates.maxOrNull() ?: maxHeartRate }
    val range = (maxHrData - minHr).coerceAtLeast(1)

    // Calculate Data for Zone Bars
    val zoneCounts = remember(heartRates) {
        val counts = IntArray(5)
        heartRates.forEach { hr ->
            val p = hr.toFloat() / maxHeartRate
            val idx = when { p < 0.6 -> 0; p < 0.7 -> 1; p < 0.8 -> 2; p < 0.9 -> 3; else -> 4 }
            counts[idx]++
        }
        counts
    }
    val maxZoneCount = zoneCounts.maxOrNull()?.coerceAtLeast(1) ?: 1

    Card(
        onClick = {}, // Non-clickable info card
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            // HEADER
            Text(
                text = "Heart Rate Analysis",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 1. THE LINE GRAPH (With Zone Gradient!)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp) // Compact height
            ) {
                val w = size.width
                val h = size.height
                val stepX = w / (heartRates.size - 1).coerceAtLeast(1)

                // Create the path
                val path = Path().apply {
                    heartRates.forEachIndexed { i, hr ->
                        val x = i * stepX
                        // Normalize Y (flip coordinate system)
                        val y = h - ((hr - minHr).toFloat() / range * h)
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                }

                // Create a Vertical Gradient that matches the Zones
                // This colors the line Red when high, Blue when low
                val gradientBrush = Brush.verticalGradient(
                    colors = zoneColors.reversed(), // Red at top, Grey at bottom
                    startY = 0f,
                    endY = h
                )

                drawPath(
                    path = path,
                    brush = gradientBrush,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. THE ZONE BARS (Miniature)
            Row(
                modifier = Modifier.fillMaxWidth().height(40.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween // Distribute evenly
            ) {
                zoneCounts.forEachIndexed { index, count ->
                    // Draw simple bars using standard Compose Box/Canvas would be overkill,
                    // let's just use a Canvas for the whole row to be performant.
                }

                // Drawing the bars inside a single Canvas for performance
                Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                    val barW = size.width / 5f - 4.dp.toPx() // 5 bars with gap
                    val maxH = size.height

                    zoneCounts.forEachIndexed { i, count ->
                        val barH = (count.toFloat() / maxZoneCount) * maxH
                        val x = i * (size.width / 5f) + 2.dp.toPx()

                        drawRoundRect(
                            color = zoneColors[i],
                            topLeft = Offset(x, maxH - barH),
                            size = Size(barW, barH),
                            cornerRadius = CornerRadius(2.dp.toPx())
                        )
                    }
                }
            }

            // Tiny Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Warmup", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text("Peak", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}