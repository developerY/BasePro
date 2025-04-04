package com.ylabz.basepro.applications.bike.ui.components.unused

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PulsingIcon(
    icon: ImageVector,
    contentDescription: String,
    color: Color = Color.Gray,
    modifier: Modifier = Modifier
) {
    // Infinite transition for pulsing scale
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f, // scale up by 10%
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = color,
        modifier = modifier
            .size(24.dp)
            .scale(scale)
    )
}
