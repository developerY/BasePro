package com.ylabz.basepro.ashbike.mobile.features.glass.ui

data class GlassUiState(
    val currentGear: Int = 1,
    // Add other Glass-specific fields here later (e.g., Speed, Heart Rate)
    val connectionStatus: String = "Connected"
)