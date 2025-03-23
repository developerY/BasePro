package com.ylabz.basepro.feature.home.ui.components.colors

import androidx.compose.ui.graphics.Color

fun randomPastelColor(): Color {
    // Each channel is between 200 and 255 to keep it on the lighter side
    val r = (200..255).random()
    val g = (200..255).random()
    val b = (200..255).random()
    return Color(r, g, b, 255)
}
