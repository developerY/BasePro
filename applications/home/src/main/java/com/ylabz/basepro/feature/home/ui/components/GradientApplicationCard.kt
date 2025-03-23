package com.ylabz.basepro.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun GradientApplicationCard(
    title: String,
    description: String,
    icon: ImageVector,
    onLaunch: (() -> Unit)? = null  // make it optional
) {
    // Define a pastel gradient
    val pastelBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF2196F3),  // very light pink
            Color(0xFFD9E4FF)   // very light purple/blue
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Let the gradient show through
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Match the Card’s shape here
                .clip(RoundedCornerShape(12.dp))
                .background(pastelBrush)
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Justify,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                // Conditionally show the Launch button
                onLaunch?.let { safeOnLaunch ->
                    Button(onClick = safeOnLaunch) {
                        Text("Launch")
                    }
                }
            }

        }
    }
}
