package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import com.ylabz.basepro.core.model.bike.SuspensionState


data class GlassUiState(
    var currentGear: Int = 1,
    val suspension: SuspensionState = SuspensionState.OPEN, // <--- NEW
    val currentSpeed: String = "0.0", // <--- Add this
    val heading: String = "---", // <--- NEW: Compass Direction
    // Add other Glass-specific fields here later (e.g., Speed, Heart Rate)
    val connectionStatus: String = "Connected",
    val batteryLevel: Int? = null, // <--- NEW: Null means unknown/disconnected
    val currentScreen: ScreenState = ScreenState.HOME
)

