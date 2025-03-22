package com.ylabz.basepro.applications.bike.ui.components.path

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import kotlin.math.roundToInt
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BigBikeProgressIndicator(
    currentDistance: Double,
    totalDistance: Double,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 8.dp,
    iconSize: Dp = 48.dp,
    containerHeight: Dp = 70.dp // enough space for half the icon above & below the line
) {
    val fraction = if (totalDistance > 0) {
        (currentDistance / totalDistance).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(containerHeight)
    ) {
        // Convert DP to pixels for precise offsets
        val density = LocalDensity.current
        val containerWidthPx = with(density) { maxWidth.toPx() }
        val containerHeightPx = with(density) { maxHeight.toPx() }
        val trackHeightPx = with(density) { trackHeight.toPx() }
        val iconSizePx = with(density) { iconSize.toPx() }

        // We'll place the bar near the center, or a bit lower
        // so there's room above & below the bar for the icon.
        val lineY = containerHeightPx / 2  // The line is horizontally centered

        // Horizontal padding so the icon doesn't clip off screen
        val horizontalPaddingPx = iconSizePx / 2
        val trackLeftX = horizontalPaddingPx
        val trackRightX = containerWidthPx - horizontalPaddingPx
        val trackWidthPx = trackRightX - trackLeftX

        // The icon’s horizontal offset is fraction * trackWidthPx
        val iconX = trackLeftX + trackWidthPx * fraction
        // To place the line behind the icon’s center, offset the icon so its center is at lineY
        val iconY = lineY - (iconSizePx / 2)

        // 1) Draw the track
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Gray background line
            drawLine(
                color = Color.LightGray,
                start = Offset(trackLeftX, lineY),
                end = Offset(trackRightX, lineY),
                strokeWidth = trackHeightPx
            )
            // Blue progress portion
            drawLine(
                color = Color(0xFF90CAF9),
                start = Offset(trackLeftX, lineY),
                end = Offset(trackLeftX + trackWidthPx * fraction, lineY),
                strokeWidth = trackHeightPx
            )
        }

        // 2) Bike icon, centered vertically on the bar
        Icon(
            imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
            contentDescription = "Trip Progress",
            tint = Color(0xFF4CAF50),
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset {
                    IntOffset(
                        x = (iconX - iconSizePx / 2).roundToInt(),  // center horizontally
                        y = iconY.roundToInt()                     // center vertically on the bar
                    )
                }
                .size(iconSize)
        )
    }
}

@Preview
@Composable
fun BigBikeProgressIndicatorPreview() {
    BigBikeProgressIndicator(
        currentDistance = 5000.0,
        totalDistance = 10000.0,
        trackHeight = 8.dp
    )
}
