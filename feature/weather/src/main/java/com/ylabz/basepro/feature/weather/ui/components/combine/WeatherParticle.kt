package com.ylabz.basepro.feature.weather.ui.components.combine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

data class WeatherParticle(
    val screenWidth: Float,
    val screenHeight: Float,
    val sizeRange: ClosedFloatingPointRange<Float>,
    val speedRange: ClosedFloatingPointRange<Float>
) {
    private val random = Random(System.nanoTime())
    val size: Float =
        random.nextFloat() * (sizeRange.endInclusive - sizeRange.start) + sizeRange.start
    var speed: Float =
        random.nextFloat() * (speedRange.endInclusive - speedRange.start) + speedRange.start
    var x: Float = random.nextFloat() * screenWidth
    var y: Float = random.nextFloat() * screenHeight

    var isSplashing: Boolean by mutableStateOf(false)
    var splashRadius: Float by mutableStateOf(0f)
    private var splashProgress by mutableStateOf(0f)

    fun move() {
        if (isSplashing) {
            splashProgress += 0.1f
            splashRadius = (splashProgress * size * 3).coerceAtMost(size * 3)
            if (splashProgress >= 1f) {
                isSplashing = false
                splashProgress = 0f
                splashRadius = 0f
            }
        } else {
            y += speed
        }
    }

    fun resetPosition(height: Float) {
        y = -size
        x = random.nextFloat() * screenWidth
        speed = random.nextFloat() * (speedRange.endInclusive - speedRange.start) + speedRange.start
    }

    fun startSplash() {
        isSplashing = true
        splashProgress = 0f
    }
}
