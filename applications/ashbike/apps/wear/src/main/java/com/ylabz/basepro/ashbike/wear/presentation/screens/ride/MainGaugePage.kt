package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

// --- PAGE 0: Your Existing Gauge UI ---
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.ylabz.basepro.ashbike.wear.presentation.components.WearSpeedometer
import com.ylabz.basepro.core.model.bike.BikeRideInfo

@Composable
fun MainGaugePage(
    rideInfo: BikeRideInfo,
    isRecording: Boolean, // Pass this in to toggle buttons
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    // Blinking animation for the "Recording" dot
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blinkAlpha"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 1. The Gauge Background
        WearSpeedometer(
            currentSpeed = rideInfo.currentSpeed.toFloat(),
            modifier = Modifier.fillMaxSize()
        )

        // 2. Center Metrics (Speed is King)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.offset(y = (-10).dp) // Shift up slightly to make room for buttons
        ) {
            // Heart Rate (Top Accessory)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Favorite, // Icons.Rounded.Favorite, // Use a heart icon
                    contentDescription = null,
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${rideInfo.heartbeat ?: "--"}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Speed (Main Hero)
            Text(
                text = String.format("%.0f", rideInfo.currentSpeed),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )

            // Units (Small caption)
            Text(
                text = "km/h",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )

            // Distance (Secondary Hero)
            Text(
                text = String.format("%.2f km", rideInfo.currentTripDistance ?: 0.0),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 3. Recording Indicator (Top Center)
        if (isRecording) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
                    .size(8.dp)
                    .alpha(alpha)
                    .background(Color.Red, CircleShape)
            )
        }

        // 4. Compact Controls (Bottom)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isRecording) {
                // Large Start Button when stopped
                Button(
                    onClick = onStart,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(Icons.Rounded.PlayArrow, "Start")
                }
            } else {
                // Smaller Stop/Pause buttons when running
                Button(
                    onClick = onStop,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.size(50.dp) // Easier to hit than text
                ) {
                    Icon(Icons.Rounded.Stop, "Stop")
                }
            }
        }
    }
}