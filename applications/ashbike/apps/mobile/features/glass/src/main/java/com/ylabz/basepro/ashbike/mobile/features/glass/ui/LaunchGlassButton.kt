package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.UsbOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.GlassButtonState


@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@OptIn(ExperimentalProjectedApi::class)
@Composable
fun LaunchGlassButton(
    isGlassSessionActive : Boolean,
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

    // 1. Safety Check: If not Android 15 (Vanilla Ice Cream), don't render anything
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) return

    val context = LocalContext.current

    // 1. Observe the "Alive" state from the Glass
    // val isGlassSessionActive by BikeStateManager.isGlassActive.collectAsStateWithLifecycle()

    // 1. Observe connection (so button disables if you unplug)
    val scope = rememberCoroutineScope()

    // 2. Observe Connection State
    // We observe this using the XR library to react instantly to plug/unplug events
    val isGlassesConnected by remember(context, scope) {
        ProjectedContext.isProjectedDeviceConnected(context, scope.coroutineContext)
    }.collectAsStateWithLifecycle(initialValue = false)

    // 3. Conditional Rendering
    // The button is only visible when the hardware is detected
    if (isGlassesConnected) {
        Button(
            onClick = onButtonClick, // CHANGED: Now delegates to the ViewModel via callback
            modifier = modifier,
            // Use a distinct color so the user knows this is a "special" action
            colors = ButtonDefaults.buttonColors(
                // 2. CHANGE COLOR based on status
                containerColor = if (isGlassSessionActive)
                    Color(0xFF4CAF50) // Green when running
                else
                    MaterialTheme.colorScheme.primary // Purple/Default when idle
            )

        ) {
            // 3. CHANGE ICON & TEXT
            Icon(
                if (isGlassSessionActive) Icons.Default.CheckCircle else Icons.Default.Visibility,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isGlassSessionActive) "Glass Active" else "Start Glass Mode")
        }
    }
    else {
        Button(
            onClick = {}, // No action needed
            enabled = false, // FIX: This automatically grays out the button and text
            modifier = modifier
        ) {
            // FIX: Switch icon to indicate "Off" state
            Icon(Icons.Default.VisibilityOff, contentDescription = null)

            Spacer(modifier = Modifier.width(8.dp))

            // Clear text indicating why it is disabled
            Text("Glass Not Connected")
        }
    }
}

// Simple data holder for the 'when' block
private data class Quad(val t: String, val i: ImageVector, val c: Color, val e: Boolean)