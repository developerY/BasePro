package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.UsbOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.GlassButtonState


@Composable
fun LaunchGlassButton(
    buttonState: GlassButtonState,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine visuals based on the 3-State Enum
    val (text, icon, containerColor, enabled) = when (buttonState) {
        GlassButtonState.NO_GLASSES -> Quad(
            "No Glasses Connected",
            Icons.Default.UsbOff,
            MaterialTheme.colorScheme.surfaceVariant, // Gray
            false
        )
        GlassButtonState.READY_TO_START -> Quad(
            "Start Glass Mode",
            Icons.Default.Visibility,
            MaterialTheme.colorScheme.primary, // Purple/App Color
            true
        )
        GlassButtonState.PROJECTING -> Quad(
            "Glass Active (Tap to Stop)",
            Icons.Default.CheckCircle,
            Color(0xFF4CAF50), // Green
            true
        )
    }

    // Render
    Button(
        onClick = onButtonClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = Color.LightGray
        ),
        modifier = modifier.height(50.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

// Simple data holder for the 'when' block
private data class Quad(val t: String, val i: ImageVector, val c: Color, val e: Boolean)