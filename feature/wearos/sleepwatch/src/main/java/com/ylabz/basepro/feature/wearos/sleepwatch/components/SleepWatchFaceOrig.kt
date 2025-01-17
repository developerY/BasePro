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

    // Remember state to store the tap location
    var tapLocation by remember { mutableStateOf<Offset?>(null) }

    val selectedSegment = remember { mutableStateOf<SleepSegment?>(null) }

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
                    val circleCenter = Offset(size.width / 2f, size.height / 2f)



                    /*val tapAngleDegrees = (-atan2(
                        x = tapOffset.y - center.y,
                        y = tapOffset.x - center.x
                    ) * (180f / Math.PI).toFloat() + 360f).mod(360f)*/

                    val tapAngleInDegrees = (
                            -atan2(
                                x = circleCenter.y - tapOffset.y,
                                y = circleCenter.x - tapOffset.x
                            ) * (180f / Math.PI).toFloat()
                            ).mod(360f)

                    val adjustedTapAngle = (tapAngleInDegrees).mod(360f)
                    // Shift by 90° to align with top as 0°.
                    // val adjustedTapAngle = (tapAngleDegrees + 90f).mod(360f)

                    tapLocation = tapOffset  // Save tap location

                    Log.d("SleepClockFaceOrig", "Tap angle: $tapAngleInDegrees")
                    Log.d("SleepClockFaceOrig", "Adjusted Tap angle: $adjustedTapAngle")


                    var foundSegment: SleepSegment? = null

                    segments.forEach { segment ->
                        val startAngle = hourToAngle(segment.startHour)
                        val endAngle = hourToAngle(segment.endHour)
                        val sweepAngle = if (endAngle >= startAngle) {
                            endAngle - startAngle
                        } else {
                            (360f - startAngle) + endAngle
                        }

                        val isTapped = if (startAngle + sweepAngle > 360f) {
                            // Handle case where the segment wraps past midnight
                            adjustedTapAngle in startAngle..360f || adjustedTapAngle in 0f..(startAngle + sweepAngle - 360f)
                        } else {
                            adjustedTapAngle in startAngle..(startAngle + sweepAngle)
                        }

                        if (isTapped) {
                            foundSegment = segment
                            return@forEach
                        }
                    }

                    if (foundSegment != null) {
                        Log.d("SleepClockFaceOrig", "Tapped on segment: ${foundSegment.label}")
                        clickedSegment = foundSegment
                    } else {
                        Log.d("SleepClockFaceOrig", "No segment tapped")
                        clickedSegment = null
                    }

                    // Detect the tapped segment
                    val tappedSegment = segments.find { segment ->
                        val startAngle = (hourToAngle(segment.startHour) - 90f).mod(360f)
                        val endAngle = (hourToAngle(segment.endHour) - 90f).mod(360f)
                        val adjustedTapAngle = tapAngleInDegrees
                        if (endAngle > startAngle) {
                            adjustedTapAngle in startAngle..endAngle
                        } else {
                            adjustedTapAngle in startAngle..360f || adjustedTapAngle in 0f..endAngle
                        }
                    }

                    if (tappedSegment != null) {
                        Log.d("SleepClockFaceOrig", "Tapped on segment: ${tappedSegment.label}")
                        selectedSegment.value = tappedSegment
                    } else {
                        Log.d("SleepClockFaceOrig", "No segment tapped")
                        selectedSegment.value = null
                    }
                }
            }
    ) {
        val center = size.center // Use center here for drawing


        // Draw a dot and a line
        tapLocation?.let {
            drawCircle(
                color = Color.Red,
                radius = 8f,
                center = it
            )
            /*drawLine( // line to help find where the tap is
                color = Color.Green,
                start = center,
                end = it,
                strokeWidth = 3f
            )*/
        }


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

        // Draw percentage for the selected segment
        selectedSegment.value?.let { segment ->
            drawContext.canvas.nativeCanvas.apply {
                val text = "${segment.label}: ${segment.percentage}%"
                val paint = Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 32f
                    textAlign = Paint.Align.CENTER
                    isFakeBoldText = true
                }
                val textBounds = android.graphics.Rect()
                paint.getTextBounds(text, 0, text.length, textBounds)

                // Calculate the vertical center offset
                val textHeight = textBounds.height()
                drawText(
                    text,
                    center.x,
                    center.y + (textHeight / 2), // Center vertically
                    paint
                )
            }
        }


        drawClockHands(currentHourFraction.value, radius, center)
    }
}



fun getHourFraction(): Float {
    val time = LocalTime.now()
    return (time.hour % 12) + (time.minute / 60f)
}

/*fun DrawScope.drawClockHandsNew(hourFraction: Float, radius: Float, center: Offset) {
    val hourAngleDegrees = (hourFraction * 30f) - 90f
    rotate(degrees = hourAngleDegrees, pivot = center) {
        drawLine(
            color = Color.Magenta,
            start = center,
            end = Offset(center.x, center.y - radius * 0.6f),
            strokeWidth = 4f
        )
    }
}*/

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

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SleepClockFaceOrigPreview() {
    val sampleSegments = listOf(
        SleepSegment(startHour = 22.0f, endHour = 23.0f, percentage = 10f, color = Color.Gray, label = "Light Sleep"),
        SleepSegment(startHour = 23.0f, endHour = 1.0f, percentage = 20f, color = Color.Green, label = "REM"),
        SleepSegment(startHour = 1.0f, endHour = 3.0f, percentage = 40f, color = Color.Blue, label = "Deep Sleep"),
        SleepSegment(startHour = 3.0f, endHour = 6.0f, percentage = 30f, color = Color.Cyan, label = "N1 Sleep"),
        SleepSegment(startHour = 6.0f, endHour = 7.0f, percentage = 30f, color = Color.White, label = "N3 Sleep"),
        SleepSegment(startHour = 8.0f, endHour = 9.0f, percentage = 30f, color = Color.Red, label = "N3 Sleep")
    )

    SleepClockFaceOrig(
        segments = sampleSegments,
        clockSize = 300.dp, // Adjust size as needed
        modifier = Modifier
    )
}

