package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import com.ylabz.basepro.core.model.bike.SuspensionState


data class GlassUiState(
    var currentGear: Int = 1,
    val suspension: SuspensionState = SuspensionState.OPEN, // <--- NEW
    val currentSpeed: String = "0.0", // <--- Add this
    val heading: String = "---", // <--- NEW: Compass Direction
    val motorPower: String = "--",   // <--- NEW: Watts
    val heartRate: String = "--",    // <--- NEW: BPM
    // Add other Glass-specific fields here later (e.g., Speed, Heart Rate)
    val connectionStatus: String = "Connected", // Glass connection is redundant
    val isBikeConnected: Boolean = false, // <--- New field
    val batteryLevel: Int? = null, // <--- NEW: Null means unknown/disconnected
    val currentScreen: ScreenState = ScreenState.HOME
)

