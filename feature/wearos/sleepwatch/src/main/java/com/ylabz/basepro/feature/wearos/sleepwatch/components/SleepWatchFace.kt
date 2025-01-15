package com.ylabz.basepro.feature.wearos.sleepwatch.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.health.SleepSegment
import java.time.LocalTime
import kotlin.collections.first
import kotlin.collections.isNotEmpty


@Composable
fun SleepClockFace(
    segments: List<SleepSegment>,
    clockSize: Dp,
    modifier: Modifier = Modifier
) {
    // For an actual 12-hour clock, 0° at top or 0° at 3 o’clock, you’ll do some angle math
    // Let's say 0 hours = 0°, 12 hours = 360°, etc. (and we offset for a top-based clock).
    // This sample uses a 12-hour conceptual approach; adjust as needed.

    val diameterPx = with(LocalDensity.current) { clockSize.toPx() }
    val radius = diameterPx / 2f

    // Current time as hour + fraction (e.g. 13.5 for 1:30 PM, but we’ll mod 12 below)
    val currentTime = remember { LocalTime.now() }
    val currentHourFraction = (currentTime.hour % 12) + (currentTime.minute / 60f)

    Canvas(
        modifier = modifier.size(clockSize)
    ) {
        // 1. Optional: Draw a background circle
        drawArc(
            color = Color.Gray.copy(alpha = 0.1f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = true,
            topLeft = Offset.Zero,
            size = Size(diameterPx, diameterPx)
        )

        // 2. Draw each sleep segment as an arc
        segments.forEach { segment ->
            // Convert startHour / endHour to angles in degrees
            val startAngleDegrees = hourToAngle(segment.startHour)
            val endAngleDegrees = hourToAngle(segment.endHour)

            // Because start could be after midnight, handle crossing 12. This is simplified.
            val sweepAngle = if (endAngleDegrees >= startAngleDegrees) {
                endAngleDegrees - startAngleDegrees
            } else {
                (360f - startAngleDegrees) + endAngleDegrees
            }

            drawArc(
                color = segment.color,
                startAngle = startAngleDegrees - 90f, // shift so 0 is at top
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset.Zero,
                size = Size(diameterPx, diameterPx),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = radius * 0.2f  // thickness
                )
            )
        }

        // 3. Draw bedtime (blue) and wake time (yellow) dots
        //    For illustration, assume the first segment’s start is bedtime, last’s end is wake time
        if (segments.isNotEmpty()) {
            val firstSegment = segments.first()
            val lastSegment = segments.last()

            drawColoredDot(
                hour = firstSegment.startHour,
                radius = radius,
                color = Color.Blue
            )
            drawColoredDot(
                hour = lastSegment.endHour,
                radius = radius,
                color = Color.Yellow
            )
        }

        // 4. Draw clock hands for current time
        //    (If you want an actual clock that matches local time)
        drawClockHands(currentHourFraction, radius)

        // 5. Optionally draw text labels for each segment’s percentage or title
        segments.forEach { segment ->
            val segmentCenterAngle = getArcCenterAngle(segment.startHour, segment.endHour)
            val labelAngle = (segmentCenterAngle - 90f)  // shift for top-based 0
            val labelOffset = 0.7f * radius
            val labelPos = polarToCartesian(labelAngle, labelOffset, center)

            // Slightly simplified text placement
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "${segment.label}\n${segment.percentage}%",
                    labelPos.x,
                    labelPos.y,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

/** Helper: convert an hour (0–12) to degrees (0–360). */
fun hourToAngle(hour: Float): Float {
    // 12 hours = 360°, so 1 hour = 30°
    // e.g. 10.5 hours => 10.5 * 30 = 315°
    return (hour % 12f) * 30f
}

/** Returns the mid-angle between start/end in [0..360). */
fun getArcCenterAngle(startHour: Float, endHour: Float): Float {
    val startDeg = hourToAngle(startHour)
    val endDeg = hourToAngle(endHour)
    val sweep = if (endDeg >= startDeg) endDeg - startDeg else 360f - startDeg + endDeg
    return (startDeg + sweep / 2f) % 360f
}

/** Draws a small circle (dot) at the given hour on the clock. */
fun DrawScope.drawColoredDot(hour: Float, radius: Float, color: Color) {
    val angleDegrees = hourToAngle(hour) - 90f
    val dotOffset = radius * 0.85f
    val dotPosition = polarToCartesian(angleDegrees, dotOffset, center)

    drawCircle(
        color = color,
        radius = 8f,
        center = dotPosition
    )
}

/** Draw clock hands for the given hour [0..12). For a minute hand, do similarly. */
fun DrawScope.drawClockHands(hourFraction: Float, radius: Float) {
    // E.g., hourFraction = 10.5 => 10:30
    val hourAngleDegrees = (hourFraction * 30f) - 90f  // shift so 0 is at top

    // Hour hand
    rotate(degrees = hourAngleDegrees, pivot = center) {
        drawLine(
            color = Color.Magenta,
            start = center,
            end = Offset(center.x, center.y - radius * 0.6f),
            strokeWidth = 4f
        )
    }

    // (Optionally) add minute hand:
    // val minutesFraction = (hourFraction % 1) * 60
    // ...
}

/** Convert angle+distance to an (x,y) on the Canvas. Angle in degrees, 0° at right by default. */
fun polarToCartesian(angleDeg: Float, distance: Float, center: Offset): Offset {
    val angleRad = Math.toRadians(angleDeg.toDouble())
    val x = center.x + distance * kotlin.math.cos(angleRad).toFloat()
    val y = center.y + distance * kotlin.math.sin(angleRad).toFloat()
    return Offset(x, y)
}


@Composable
@Preview(showBackground = true, backgroundColor = 0xFF000000)
fun SleepClockFacePreview() {
    val sampleSegments = listOf(
        // Start/End in decimal hours: e.g., 22.5 = 10:30 PM
        SleepSegment(startHour = 22.5f, endHour = 23.5f, percentage = 8f, color = Color(0xFF6A5ACD), label = "N2 Sleep: 2"),
        SleepSegment(startHour = 23.5f, endHour = 1.0f,  percentage = 16f, color = Color(0xFF7B68EE), label = "REM: 1"),
        SleepSegment(startHour = 1.0f,  endHour = 3.0f,  percentage = 33f, color = Color(0xFF483D8B), label = "Deep: 2"),
        SleepSegment(startHour = 3.0f,  endHour = 6.0f,  percentage = 30f, color = Color(0xFF708090), label = "N1 Sleep"),
        SleepSegment(startHour = 6.0f,  endHour = 7.0f,  percentage = 8f,  color = Color(0xFF9370DB), label = "Light Sleep")
    )

    // This is your main screen composable

    SleepClockFace(
        segments = sampleSegments,
        clockSize = 150.dp
    )


}
