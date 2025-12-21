package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import com.ylabz.basepro.core.model.bike.SuspensionState


data class GlassUiState(
    var currentGear: Int = 1,
    val suspension: SuspensionState = SuspensionState.OPEN, // <--- NEW
    val currentSpeed: String = "0.0", // <--- Add this
    val heading: String = "---", // <--- NEW: Compass Direction
    val motorPower: String = "--",   // <--- NEW: Watts
    val heartRate: String = "--",    // <--- NEW: BPM
    val distance: String = "0.0",    // <--- NEW: Miles/KM
    // Add other Glass-specific fields here later (e.g., Speed, Heart Rate)
    val connectionStatus: String = "Connected", // Glass connection is redundant
    val tripDistance: String = "0.0", // <--- NEW
    val calories: String = "0",       // <--- NEW
    val rideDuration: String = "00:00", // <--- NEW
    val averageSpeed: String = "0.0",   // <--- NEW
    val isBikeConnected: Boolean = false, // <--- New field
    val batteryLevel: Int? = null, // <--- NEW: Null means unknown/disconnected
    val currentScreen: ScreenState = ScreenState.HOME
)

