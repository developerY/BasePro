package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.BatteryStatusDisplay
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.GearControlPanel
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.GlassColors
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.HomeScreen
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.MetricDisplay
import com.ylabz.basepro.core.model.bike.SuspensionState // Ensure this matches your actual package

// -------------------------------------------------------------------------
// 1. COMPONENT PREVIEWS (Isolate the pieces)
// -------------------------------------------------------------------------

@Preview(name = "Metric Display (Speed)", showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MetricDisplayPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(20.dp).background(Color.Black)) {
            MetricDisplay(
                label = "SPEED (MPH)",
                value = "37.0",
                subValue = "113° SE",
                highlightColor = GlassColors.NeonCyan
            )
        }
    }
}

@Preview(name = "Gear Control Panel", showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun GearControlPanelPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(20.dp).background(Color.Black)) {
            // We need a FocusRequester for the preview, even if we don't click it
            val focusRequester = remember { FocusRequester() }

            GearControlPanel(
                currentGear = 8,
                onGearUp = {},
                onGearDown = {},
                focusRequester = focusRequester
            )
        }
    }
}

// -------------------------------------------------------------------------
// 2. FULL SCREEN HUD PREVIEW
// -------------------------------------------------------------------------

@Preview(
    name = "Full HUD (640x360)",
    device = "id:ai_glasses_device"
)
@Composable
fun HomeScreenScreenPreview() {
    val sampleState = GlassUiState(
        currentGear = 7,
        currentSpeed = "24.5",
        heading = "350° N",
        suspension = SuspensionState.TRAIL,
        connectionStatus = "Connected"
    )

    MaterialTheme {
        HomeScreen(
            uiState = sampleState,
            onEvent = {} // No-op
        )
    }
}

@Preview(name = "Battery States",
    device = "id:ai_glasses_device"
)
@Composable
fun BatteryPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(20.dp).background(Color.Black)) {
            BatteryStatusDisplay(level = 85) // Green
            Spacer(Modifier.height(8.dp))
            BatteryStatusDisplay(level = 40) // Orange
            Spacer(Modifier.height(8.dp))
            BatteryStatusDisplay(level = 10) // Red
            Spacer(Modifier.height(8.dp))
            BatteryStatusDisplay(level = null) // Gray
        }
    }
}