package com.ylabz.basepro.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColors(
    // Previous custom colors if they exist
    val speedSlow: Color = Color.Unspecified,
    val speedMedium: Color = Color.Unspecified,
    val speedFast: Color = Color.Unspecified,

    // New properties for settings card backgrounds
    val settingsProfile: Color = Color.Unspecified,
    val settingsNfc: Color = Color.Unspecified,
    val settingsTheme: Color = Color.Unspecified,
    val settingsBike: Color = Color.Unspecified
)

val LocalCustomColors = staticCompositionLocalOf { CustomColors() }