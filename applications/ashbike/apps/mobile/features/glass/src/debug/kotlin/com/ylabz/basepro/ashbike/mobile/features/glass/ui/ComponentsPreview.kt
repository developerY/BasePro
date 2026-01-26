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
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.status.BatteryStatusDisplay
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.status.BikeConnectionStatus
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.panels.drafts.GearControlPanel
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.screens.HomeScreen
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.telemetry.MetricDisplay
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.stage.DataPill
import com.ylabz.basepro.core.model.bike.SuspensionState

// -------------------------------------------------------------------------
// 1. COMPONENT PREVIEWS (Isolate the pieces)
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
                // UPDATED: Pass Zone + Text manually for preview
                BatteryStatusDisplay(zone = BatteryZone.GOOD, levelText = "85%")
            }

            Spacer(Modifier.height(20.dp))

            // Case B: Disconnected & Low Battery (Red/Orange)
            Row(verticalAlignment = Alignment.CenterVertically) {
                BikeConnectionStatus(isConnected = false)
                Spacer(Modifier.width(10.dp))
                // UPDATED: Pass Zone + Text manually for preview
                BatteryStatusDisplay(zone = BatteryZone.CRITICAL, levelText = "15%")
            }
        }
    }
}

@Preview(name = "Gear Control Panel", showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun GearControlPanelPreview() {
    MaterialTheme {
        Box(modifier = Modifier
            .padding(20.dp)
            .background(Color.Black)) {
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
        rawSpeed = 24.5,
        rawHeading = 350f,
        suspension = SuspensionState.TRAIL,
        isBikeConnected = true
    )

    MaterialTheme {
        HomeScreen(
            uiState = sampleState,
            onEvent = {}
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
            modifier = Modifier
                .padding(20.dp)
                .background(Color.Black)
        ) {
            // UPDATED: Use the Enum + Text API
            BatteryStatusDisplay(zone = BatteryZone.GOOD, levelText = "85%")
            Spacer(Modifier.height(8.dp))
            BatteryStatusDisplay(zone = BatteryZone.WARNING, levelText = "40%")
            Spacer(Modifier.height(8.dp))
            BatteryStatusDisplay(zone = BatteryZone.CRITICAL, levelText = "10%")
            Spacer(Modifier.height(8.dp))
            BatteryStatusDisplay(zone = BatteryZone.UNKNOWN, levelText = "--")
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
        Column(modifier = Modifier
            .background(Color.Black)
            .padding(10.dp)) {
            // Connected State
            BikeConnectionStatus(isConnected = true)
            BatteryStatusDisplay(zone = BatteryZone.GOOD, levelText = "88%")

            Spacer(Modifier.height(20.dp))

            // Disconnected State
            BikeConnectionStatus(isConnected = false)
            BatteryStatusDisplay(zone = BatteryZone.CRITICAL, levelText = "12%")
        }
    }
}

// -------------------------------------------------------------------------
// 3. FULL SCREEN HUD PREVIEW (Example 2)
// -------------------------------------------------------------------------

@Preview(
    name = "Full HUD (640x360) - Active",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 640,
    heightDp = 360
)
@Composable
fun HomeScreenScreen2Preview() {
    val sampleState = GlassUiState(
        currentGear = 7,
        rawSpeed = 24.5,
        rawHeading = 350f,
        suspension = SuspensionState.TRAIL,
        isBikeConnected = true,
        rawBattery = 92 // This will be calculated to BatteryZone.GOOD automatically inside HomeScreen
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
        Box(modifier = Modifier
            .background(Color.Black)
            .padding(20.dp)) {
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
        rawSpeed = 24.5,
        rawHeading = 350f,
        currentGear = 7,
        rawMotorPower = 250.0,
        rawHeartRate = 145,
        isBikeConnected = true,
        rawBattery = 90
    )
    MaterialTheme {
        HomeScreen(uiState = sampleState, onEvent = {})
    }
}

@Preview(name = "HUD - Disconnected (Stats Mode)", widthDp = 640, heightDp = 360)
@Composable
fun HudDisconnectedPreview() {
    val sampleState = GlassUiState(
        rawSpeed = 12.4,
        rawHeading = 90f,
        isBikeConnected = false,
        tripDistance = "15.2",
        calories = "450",
        rawMotorPower = 0.0,
        rawHeartRate = 130
    )
    MaterialTheme {
        HomeScreen(uiState = sampleState, onEvent = {})
    }
}