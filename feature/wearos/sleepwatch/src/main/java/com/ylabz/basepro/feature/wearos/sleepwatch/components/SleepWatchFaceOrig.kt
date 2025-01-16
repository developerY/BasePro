package com.ylabz.basepro.feature.wearos.sleepwatch.components

import android.R.attr.textSize
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color


import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.ylabz.basepro.core.model.health.SleepSegment
import kotlinx.coroutines.delay
import java.time.LocalTime
import kotlin.collections.first
import kotlin.collections.isNotEmpty
import kotlin.math.PI
import kotlin.math.atan2


@Composable
fun SleepClockFaceOrig(
    segments: List<SleepSegment>,
    clockSize: Dp,
    modifier: Modifier = Modifier
) {
    val diameterPx = with(LocalDensity.current) { clockSize.toPx() }
    val radius = diameterPx / 2f
    val currentHourFraction = remember { mutableStateOf(getHourFraction()) }

    // Periodically update the time (e.g., every minute)
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L) // 1 minute in milliseconds
            currentHourFraction.value = getHourFraction()
        }
    }

    var clickedSegment by remember { mutableStateOf<SleepSegment?>(null) }

    Canvas(
        modifier = modifier
            .size(clockSize)
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val center = size.center // Use size.center directly
                    val tapAngleDegrees = (-atan2(
                        x = center.y - tapOffset.y,
                        y = center.x - tapOffset.x
                    ) * (180f / PI).toFloat() - 90f).mod(360f)

                    Log.d("SleepClockFaceOrig", "Tap angle: $tapAngleDegrees")

                    // Check if the tap is inside any segment
                    var found = false
                    segments.forEach { segment ->
                        val startAngle = hourToAngle(segment.startHour)
                        val endAngle = hourToAngle(segment.endHour)
                        val sweepAngle = if (endAngle >= startAngle) {
                            endAngle - startAngle
                        } else {
                            (360f - startAngle) + endAngle
                        }

                        val isTapped = tapAngleDegrees in startAngle..(startAngle + sweepAngle)
                        if (isTapped) {
                            Log.d("SleepClockFaceOrig", "Tapped on segment: ${segment.label}")
                            clickedSegment = segment
                            found = true
                            return@detectTapGestures
                        }
                    }

                    if (!found) {
                        Log.d("SleepClockFaceOrig", "No segment tapped")
                        clickedSegment = null
                    }
                }
            }
    ) {
        val center = size.center // Use center here for drawing

        // Drawing arcs and other elements using 'center'
        drawArc(
            color = Color.Gray.copy(alpha = 0.1f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = true,
            size = Size(diameterPx, diameterPx),
            topLeft = Offset.Zero
        )

        segments.forEach { segment ->
            val startAngleDegrees = hourToAngle(segment.startHour)
            val endAngleDegrees = hourToAngle(segment.endHour)
            val sweepAngle = if (endAngleDegrees >= startAngleDegrees) {
                endAngleDegrees - startAngleDegrees
            } else {
                (360f - startAngleDegrees) + endAngleDegrees
            }

            drawArc(
                color = if (clickedSegment == segment) Color.Yellow else segment.color,
                startAngle = startAngleDegrees - 90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                size = Size(diameterPx, diameterPx),
                style = Stroke(width = radius * 0.2f)
            )
        }

        drawClockHands(currentHourFraction.value, radius, center)
    }
}



fun getHourFraction(): Float {
    val time = LocalTime.now()
    return (time.hour % 12) + (time.minute / 60f)
}

fun DrawScope.drawClockHandsNew(hourFraction: Float, radius: Float, center: Offset) {
    val hourAngleDegrees = (hourFraction * 30f) - 90f
    rotate(degrees = hourAngleDegrees, pivot = center) {
        drawLine(
            color = Color.Magenta,
            start = center,
            end = Offset(center.x, center.y - radius * 0.6f),
            strokeWidth = 4f
        )
    }
}

/** Convert angle+distance to an (x,y) on the Canvas. Angle in degrees, 0° at right by default. */
fun polarToCartesian(angleDeg: Float, distance: Float, center: Offset): Offset {
    val angleRad = Math.toRadians(angleDeg.toDouble())
    val x = center.x + distance * kotlin.math.cos(angleRad).toFloat()
    val y = center.y + distance * kotlin.math.sin(angleRad).toFloat()
    return Offset(x, y)
}

/** Convert an hour (decimal) [0–12] to degrees [0–360]. */
fun hourToAngle(hour: Float): Float {
    // 12 hours -> 360°, so 1 hour -> 30°
    return (hour % 12f) * 30f
}


/** Helper function to calculate the sweep angle for a sleep segment. */
fun calculateSweepAngle(segment: SleepSegment): Float {
    val startAngleDegrees = hourToAngle(segment.startHour)
    val endAngleDegrees = hourToAngle(segment.endHour)
    return if (endAngleDegrees >= startAngleDegrees) {
        endAngleDegrees - startAngleDegrees
    } else {
        (360f - startAngleDegrees) + endAngleDegrees
    }
}
