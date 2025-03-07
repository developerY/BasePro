package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedHeartRateCard(
    heartRate: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(100.dp)
            .height(80.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.8f)
        )
    ) {
        // A small infinite transition that pulses the icon’s scale
        val infiniteTransition = rememberInfiniteTransition()
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f, // scale up 10%
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Pulsing heart icon
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Heart Rate",
                tint = Color.Red,
                modifier = Modifier
                    .size(20.dp)
                    .scale(scale) // apply the pulsing scale
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${heartRate} bpm",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Heart Rate",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.DarkGray)
            )
        }
    }
}
