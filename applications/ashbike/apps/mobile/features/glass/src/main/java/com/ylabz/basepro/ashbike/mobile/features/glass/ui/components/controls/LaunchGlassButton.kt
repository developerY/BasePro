package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.controls

import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.PermDeviceInformation
import androidx.compose.material.icons.filled.UsbOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.ashbike.mobile.features.glass.R
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.model.GlassButtonState

@Composable
fun LaunchGlassButton(
    buttonState: GlassButtonState,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    // Debug Recomposition
    SideEffect {
        Log.d("DEBUG_GLASS", "3. UI Recomposition: Button State = $buttonState")
    }

    // 1. Safety Check for Android 15
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) return

    // 2. Define the Visuals for each State
    data class StateVisuals(
        val text: String,
        val icon: ImageVector,
        val containerColor: Color,
        val contentColor: Color,
        val enabled: Boolean
    )

    val visuals = when (buttonState) {
        GlassButtonState.NO_GLASSES -> StateVisuals(
            text = stringResource(R.string.connect_glasses),
            icon = Icons.Default.UsbOff,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
            enabled = false
        )
        GlassButtonState.READY_TO_START -> StateVisuals(
            text = stringResource(R.string.start_projection),
            icon = Icons.Default.PermDeviceInformation, // Or generic "Glasses" icon
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            enabled = true
        )
        GlassButtonState.PROJECTING -> StateVisuals(
            text = stringResource(R.string.projecting_active),
            icon = Icons.Default.CastConnected,
            containerColor = Color(0xFF4CAF50), // Bike Green
            contentColor = Color.White,
            enabled = true // Kept enabled so user can click to "Stop" or "Open Controls"
        )
    }

    // 3. Render
    Button(
        onClick = onButtonClick,
        enabled = visuals.enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = visuals.containerColor,
            contentColor = visuals.contentColor,
            disabledContainerColor = visuals.containerColor,
            disabledContentColor = visuals.contentColor
        ),
        modifier = modifier
    ) {
        Icon(visuals.icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(visuals.text)
    }
}