package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.BatteryStatusDisplay
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.BikeConnectionStatus
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.GearControlPanel
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.HomeScreen
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.MetricDisplay
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.stage.DataPill
import com.ylabz.basepro.core.model.bike.SuspensionState

// -------------------------------------------------------------------------
// 1. COMPONENT PREVIEWS (Isolate the pieces)
// -------------------------------------------------------------------------

// -------------------------------------------------------------------------
// 1. STATUS ICONS PREVIEW (New!)
// -------------------------------------------------------------------------

@Preview(name = "Status Indicators", showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun StatusIndicatorsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(Color.Black)
                .padding(20.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Case A: Everything Good (Green)
            Row(verticalAlignment = Alignment.CenterVertically) {
                BikeConnectionStatus(isConnected = true)
                Spacer(Modifier.width(10.dp))
                BatteryStatusDisplay(level = 85)
            }

            Spacer(Modifier.height(20.dp))

            // Case B: Disconnected & Low Battery (Red/Orange)
            Row(verticalAlignment = Alignment.CenterVertically) {
                BikeConnectionStatus(isConnected = false)
                Spacer(Modifier.width(10.dp))
                BatteryStatusDisplay(level = 15)
            }
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

@Preview(
    name = "Battery States",
    device = "id:ai_glasses_device"
)
@Composable
fun BatteryPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(20.dp).background(Color.Black)
        ) {
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

@Preview(
    name = "Header Status Area",
    device = "id:ai_glasses_device"
)
@Composable
fun HeaderPreview() {
    MaterialTheme {
        Column(modifier = Modifier.background(Color.Black).padding(10.dp)) {
            // Connected State
            BikeConnectionStatus(isConnected = true)
            BatteryStatusDisplay(level = 88)

            Spacer(Modifier.height(20.dp))

            // Disconnected State
            BikeConnectionStatus(isConnected = false)
            BatteryStatusDisplay(level = 12)
        }
    }
}

// -------------------------------------------------------------------------
// 3. FULL SCREEN HUD PREVIEW
// -------------------------------------------------------------------------

@Preview(
    name = "Full HUD (640x360)",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 640,
    heightDp = 360
)
@Composable
fun HomeScreenScreen2Preview() {
    val sampleState = GlassUiState(
        currentGear = 7,
        currentSpeed = "24.5",
        heading = "350° N",
        suspension = SuspensionState.TRAIL,
        connectionStatus = "Connected",
        isBikeConnected = true, // Shows Green Bike Icon
        batteryLevel = 92       // Shows Green Battery
    )

    MaterialTheme {
        HomeScreen(
            uiState = sampleState,
            onEvent = {}
        )
    }
}

@Preview(name = "Telemetry Card with Power")
@Composable
fun TelemetryPreview() {
    MaterialTheme {
        Box(modifier = Modifier.background(Color.Black).padding(20.dp)) {
            MetricDisplay(
                label = "SPEED",
                value = "24.5",
                bottomContent = {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DataPill(Icons.Default.Explore, "350° N", Color.White)
                        DataPill(Icons.Default.Bolt, "250 W", Color(0xFFFFD600))
                    }
                }
            )
        }
    }
}

@Preview(name = "Quad Layout HUD", device = "id:ai_glasses_device")
@Composable
fun QuadHudPreview() {
    val sampleState = GlassUiState(
        currentSpeed = "24.5",
        heading = "350° N",
        currentGear = 7,
        motorPower = "250",
        heartRate = "145", // Orange Zone
        isBikeConnected = true,
        batteryLevel = 90
    )
    MaterialTheme {
        HomeScreen(uiState = sampleState, onEvent = {})
    }
}

@Preview(name = "HUD - Disconnected (Stats Mode)", widthDp = 640, heightDp = 360)
@Composable
fun HudDisconnectedPreview() {
    val sampleState = GlassUiState(
        currentSpeed = "12.4",
        heading = "90° E",
        isBikeConnected = false, // <--- Disconnected
        tripDistance = "15.2",
        calories = "450",
        motorPower = "0", // Likely 0 if disconnected
        heartRate = "130"
    )
    MaterialTheme {
        HomeScreen(uiState = sampleState, onEvent = {})
    }
}