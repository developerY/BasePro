package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import com.ylabz.basepro.ashbike.mobile.features.glass.data.SuspensionState

data class GlassUiState(
    var currentGear: Int = 1,
    val suspension: SuspensionState = SuspensionState.OPEN, // <--- NEW
    // Add other Glass-specific fields here later (e.g., Speed, Heart Rate)
    val connectionStatus: String = "Connected",
    val currentScreen: ScreenState = ScreenState.HOME
)

