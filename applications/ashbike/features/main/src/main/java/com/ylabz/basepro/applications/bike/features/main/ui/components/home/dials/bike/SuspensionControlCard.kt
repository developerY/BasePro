package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune // Good icon for suspension/tuning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SuspensionControlCard(
    suspensionState: String, // "OPEN", "TRAIL", or "LOCK"
    onToggleSuspension: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Dynamic Color: Green for Open, Orange for Trail, Red for Lock
    val statusColor = when (suspensionState.uppercase()) {
        "OPEN" -> Color(0xFF4CAF50) // Green
        "TRAIL" -> Color(0xFFFF9800) // Orange
        "LOCK" -> Color(0xFFF44336) // Red
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier.clickable { onToggleSuspension() }, // Make whole card clickable
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Side: Icon & Label
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Suspension",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Suspension",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = suspensionState,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Right Side: Status Indicator Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(statusColor.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "CHANGE",
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}