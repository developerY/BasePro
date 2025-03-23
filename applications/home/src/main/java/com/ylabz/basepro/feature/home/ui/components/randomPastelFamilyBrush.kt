package com.ylabz.basepro.feature.home.ui.components

import androidx.compose.ui.graphics.Brush
import kotlin.random.Random

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

/**
 * Convert an HSL color (0..360 for hue, 0..1 for saturation/lightness) to a Compose [Color].
 */
fun hslToColor(h: Float, s: Float, l: Float): Color {
    // chroma
    val c = (1f - abs(2f * l - 1f)) * s
    // secondary component
    val x = c * (1f - abs((h / 60f) % 2f - 1f))
    // match component
    val m = l - c / 2f

    val (rPrime, gPrime, bPrime) = when {
        h < 60f  -> Triple(c, x, 0f)
        h < 120f -> Triple(x, c, 0f)
        h < 180f -> Triple(0f, c, x)
        h < 240f -> Triple(0f, x, c)
        h < 300f -> Triple(x, 0f, c)
        else     -> Triple(c, 0f, x)
    }

    val r = rPrime + m
    val g = gPrime + m
    val b = bPrime + m
    return Color(r, g, b, alpha = 1f)
}


fun randomPastelFamilyBrush(): Brush {
    // 1) Pick a random base in pastel range
    val baseHue = Random.nextFloat() * 360f
    val baseSaturation = 0.3f + Random.nextFloat() * 0.2f  // 0.3..0.5
    val baseLightness = 0.8f + Random.nextFloat() * 0.15f  // 0.8..0.95

    // 2) Generate 5 colors with small offsets
    val colors = List(5) {
        val hueOffset = Random.nextFloat() * 10f - 5f       // ±5 degrees around base hue
        val satOffset = Random.nextFloat() * 0.1f - 0.05f   // ±0.05 around base saturation
        val lightOffset = Random.nextFloat() * 0.1f - 0.05f // ±0.05 around base lightness

        val newHue = (baseHue + hueOffset).coerceIn(0f, 360f)
        val newSaturation = (baseSaturation + satOffset).coerceIn(0f, 1f)
        val newLightness = (baseLightness + lightOffset).coerceIn(0f, 1f)

        hslToColor(newHue, newSaturation, newLightness)
    }

    // 3) Return a linear gradient
    return Brush.linearGradient(colors)
}
