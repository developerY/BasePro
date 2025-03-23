package com.ylabz.basepro.feature.home.ui.components

import androidx.compose.ui.graphics.Brush

fun randomPastelBrush(): Brush {
    // Generate 5 random pastel colors
    val colors = List(5) { randomPastelColor() }
    return Brush.linearGradient(colors)
}
