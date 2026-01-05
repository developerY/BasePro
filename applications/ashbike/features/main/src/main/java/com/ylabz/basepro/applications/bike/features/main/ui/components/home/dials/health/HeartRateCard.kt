package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.health

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeartRateCard(
    bpm: Int?,
    modifier: Modifier = Modifier
) {
    // 1. Dynamic Color based on Zone
    val cardColor by animateColorAsState(
        targetValue = when {
            bpm == null -> MaterialTheme.colorScheme.surfaceVariant
            bpm < 100 -> Color(0xFF4CAF50) // Green (Warm up)
            bpm < 130 -> Color(0xFFFFC107) // Yellow (Fat Burn)
            bpm < 160 -> Color(0xFFFF9800) // Orange (Cardio)
            else -> Color(0xFFF44336)      // Red (Peak)
        },
        label = "HeartRateColor"
    )

    val textColor = if (bpm == null) MaterialTheme.colorScheme.onSurfaceVariant else Color.White

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Icon and Label
            Column {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Heart Rate",
                    tint = textColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "HEART RATE",
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor.copy(alpha = 0.8f)
                )
            }

            // Right: Big Value
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = bpm?.toString() ?: "--",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "bpm",
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}