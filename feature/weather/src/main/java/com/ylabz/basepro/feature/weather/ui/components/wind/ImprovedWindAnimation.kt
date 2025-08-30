package com.ylabz.basepro.feature.weather.ui.components.wind

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.delay

@Composable
fun ImprovedWindAnimation(windDegree: Float) {
    // Set a fixed canvas size for the simulation (or pass actual size)
    val screenWidth = 500f
    val screenHeight = 500f

    // Create a list of wind arrows (you can adjust the count as needed)
    val windArrows = remember {
        List(10) {
            WindArrow(
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                sizeRange = 10f..30f,
                speedRange = 2f..5f,
                windDegree = windDegree
            )
        }
    }

    // Animate the wind arrows continuously.
    LaunchedEffect(Unit) {
        while (true) {
            windArrows.forEach { arrow ->
                arrow.move()
                // Reset arrow position when it moves off-screen
                if (arrow.x > screenWidth) {
                    arrow.resetPosition()
                }
            }
            delay(16L)  // ~60 FPS
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        windArrows.forEach { arrow ->
            drawArrow(
                x = arrow.x,
                y = arrow.y,
                size = arrow.size,
                rotation = arrow.getRotation()
            )
        }
    }
}

/**
 * Draws an arrow with an arrowhead.
 *
 * @param x The starting x position.
 * @param y The starting y position.
 * @param size The length of the arrow.
 * @param rotation The rotation angle in radians.
 */
fun DrawScope.drawArrow(x: Float, y: Float, size: Float, rotation: Float) {
    // Calculate the tip of the arrow
    val tipX = x + size * cos(rotation)
    val tipY = y + size * sin(rotation)

    // Draw the main shaft of the arrow
    drawLine(
        color = Color.Blue,
        start = Offset(x, y),
        end = Offset(tipX, tipY),
        strokeWidth = 4f
    )

    // Define the size of the arrowhead
    val headSize = size / 3

    // Calculate the angles for the arrowhead lines (150° offset from the shaft)
    val leftAngle = rotation + Math.toRadians(150.0).toFloat()
    val rightAngle = rotation - Math.toRadians(150.0).toFloat()

    // Calculate endpoints for arrowhead lines
    val leftX = tipX + headSize * cos(leftAngle)
    val leftY = tipY + headSize * sin(leftAngle)
    val rightX = tipX + headSize * cos(rightAngle)
    val rightY = tipY + headSize * sin(rightAngle)

    // Draw the arrowhead lines
    drawLine(
        color = Color.Blue,
        start = Offset(tipX, tipY),
        end = Offset(leftX, leftY),
        strokeWidth = 4f
    )
    drawLine(
        color = Color.Blue,
        start = Offset(tipX, tipY),
        end = Offset(rightX, rightY),
        strokeWidth = 4f
    )
}

/**
 * A simple WindArrow class to represent a wind arrow with randomized size and speed.
 */
class WindArrow(
    private val screenWidth: Float,
    private val screenHeight: Float,
    private val sizeRange: ClosedFloatingPointRange<Float>,
    private val speedRange: ClosedFloatingPointRange<Float>,
    private val windDegree: Float  // in degrees
) {
    private val random = Random(System.nanoTime())
    val size: Float = random.nextFloat() * (sizeRange.endInclusive - sizeRange.start) + sizeRange.start
    var speed: Float = random.nextFloat() * (speedRange.endInclusive - speedRange.start) + speedRange.start
    var x: Float = random.nextFloat() * screenWidth
    var y: Float = random.nextFloat() * screenHeight

    /**
     * Moves the arrow horizontally. (You could also incorporate vertical motion or wiggle effects.)
     */
    fun move() {
        x += speed
    }

    /**
     * Resets the arrow's position when it moves off-screen.
     */
    fun resetPosition() {
        x = -size  // Start off-screen on the left
        y = random.nextFloat() * screenHeight
        speed = random.nextFloat() * (speedRange.endInclusive - speedRange.start) + speedRange.start
    }

    /**
     * Returns the rotation in radians, based on the wind degree.
     */
    fun getRotation(): Float {
        return Math.toRadians(windDegree.toDouble()).toFloat()
    }
}

/*
@Preview(showBackground = true, showSystemUi = false)
@Composable
fun ImprovedWindAnimationPreview() {
    // Provide a sample value for windDegree
    ImprovedWindAnimation(windDegree = 45f)
}
*/