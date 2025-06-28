package com.ylabz.basepro.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColors(
    val speedSlow: Color = Color.Unspecified,
    val speedMedium: Color = Color.Unspecified,
    val speedFast: Color = Color.Unspecified
)

val LocalCustomColors = staticCompositionLocalOf { CustomColors() }