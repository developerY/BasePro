package com.ylabz.basepro.feature.wearos.sleepwatch.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
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
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val clockSize = screenWidth * 0.8f

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(clockSize)
        ) {
            val diameterPx = clockSize.toPx()
            val radius = diameterPx / 2f
            val clockCenter = center

            drawArc(
                color = Color.Gray.copy(alpha = 0.1f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = true,
                topLeft = Offset(clockCenter.x - radius, clockCenter.y - radius),
                size = Size(2 * radius, 2 * radius)
            )

            segments.forEach { segment ->
                val startAngle = hourToAngle(segment.startHour) - 90f
                val endAngle = hourToAngle(segment.endHour) - 90f
                val sweepAngle = if (endAngle >= startAngle) endAngle - startAngle else 360f - startAngle + endAngle

                drawArc(
                    color = segment.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(clockCenter.x - radius, clockCenter.y - radius),
                    size = Size(2 * radius, 2 * radius),
                    style = Stroke(width = radius * 0.15f)
                )
            }
        }
    }
}


fun DrawScope.drawColoredDot(center: Offset, hour: Float, radius: Float, color: Color) {
    val angleDegrees = hourToAngle(hour) - 90f
    val dotOffset = radius * 0.85f
    val dotPosition = polarToCartesian(angleDegrees, dotOffset, center)

    drawCircle(
        color = color,
        radius = 8f,
        center = dotPosition
    )
}

fun DrawScope.drawClockHands(hourFraction: Float, radius: Float, center: Offset) {
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

@Composable
@Preview(showBackground = true, backgroundColor = 0xFF000000)
fun SleepClockFacePreview() {
    val sampleSegments = listOf(
        SleepSegment(22.5f, 23.5f, 8f, Color(0xFF6A5ACD), "N2 Sleep: 2"),
        SleepSegment(23.5f, 1.0f, 16f, Color(0xFF7B68EE), "REM: 1"),
        SleepSegment(1.0f, 3.0f, 33f, Color(0xFF483D8B), "Deep: 2"),
        SleepSegment(3.0f, 6.0f, 30f, Color(0xFF708090), "N1 Sleep"),
        SleepSegment(6.0f, 7.0f, 8f, Color(0xFF9370DB), "Light Sleep")
    )

    SleepClockFace(
        segments = sampleSegments,
    )
}
