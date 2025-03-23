package com.ylabz.basepro.feature.home.ui.components.colors

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun randomPastelBrushFull(): Brush {
    val pastelBrushes = listOf(
        // Soft pink to lavender
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFDE4E4), // Light pink
                Color(0xFFF1D2FD)  // Light lavender
            )
        ),
        // Pastel yellow to light teal
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFF8F2CB), // Light pastel yellow
                Color(0xFFCCFFEE)  // Light pastel teal
            )
        ),
        // Cream to mint
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFF4BA), // Light cream
                Color(0xFFADFFCF)  // Light pastel mint
            )
        ),
        // Pink to baby blue
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFF6CCCC), // Light pink
                Color(0xFFB6CDFF)  // Light baby blue
            )
        )
    )
    return pastelBrushes.random()
}
