package com.ylabz.basepro.feature.weather.ui.components.backgrounds

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
////import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlin.random.Random

// Data class representing a single snowflake.
data class Snowflake(
    val screenWidth: Float,
    val screenHeight: Float,
    val sizeRange: ClosedFloatingPointRange<Float>,
    val speedRange: ClosedFloatingPointRange<Float>
) {
    private val random = Random(System.nanoTime())

    // Determine the snowflake's size randomly within the range.
    val size: Float =
        random.nextFloat() * (sizeRange.endInclusive - sizeRange.start) + sizeRange.start

    // Determine the fall speed randomly within the range.
    var speed: Float =
        random.nextFloat() * (speedRange.endInclusive - speedRange.start) + speedRange.start

    // Initial random position for the snowflake.
    var x: Float = random.nextFloat() * screenWidth
    var y: Float = random.nextFloat() * screenHeight

    // Move the snowflake downward by its speed.
    fun move() {
        y += speed
    }

    // Reset the snowflake to the top once it goes off-screen.
    fun resetPosition(height: Float) {
        y = -size
        x = random.nextFloat() * screenWidth
        speed = random.nextFloat() * (speedRange.endInclusive - speedRange.start) + speedRange.start
    }
}

@Composable
fun SnowBackgroundAnimation(
    modifier: Modifier = Modifier
) {
    // Default dimensions for simulation; adjust as needed or make dynamic.
    val defaultScreenWidth = 600f
    val defaultScreenHeight = 200f

    // Create a list of snowflakes for the animation.
    val snowflakes = remember {
        List(150) {
            Snowflake(
                screenWidth = defaultScreenWidth,
                screenHeight = defaultScreenHeight,
                sizeRange = 1f..3f,       // Snowflake sizes (in pixels)
                speedRange = 1f..4f        // Falling speed
            )
        }
    }

    // Animate the snowflakes in an infinite loop.
    LaunchedEffect(Unit) {
        while (true) {
            snowflakes.forEach { snowflake ->
                snowflake.move()
                // If the snowflake has fallen far below the visible area, reset its position.
                if (snowflake.y > defaultScreenHeight * 3) {
                    snowflake.resetPosition(defaultScreenHeight)
                }
            }
            delay(16L) // Approximately 60 frames per second.
        }
    }

    // Draw the falling snowflakes on a Canvas.
    Canvas(modifier = modifier.fillMaxSize()) {
        snowflakes.forEach { snowflake ->
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = snowflake.size,
                center = Offset(snowflake.x, snowflake.y)
            )
        }
    }
}
/*
@Preview
@Composable
fun SnowBackgroundAnimationPreview() {
    SnowBackgroundAnimation()
}
*/
