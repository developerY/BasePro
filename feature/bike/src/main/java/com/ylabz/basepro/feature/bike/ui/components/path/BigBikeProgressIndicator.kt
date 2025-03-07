package com.ylabz.basepro.feature.bike.ui.components.path

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
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
    currentDistance: Double,  // how far the rider has gone
    totalDistance: Double,    // total distance of the route
    modifier: Modifier = Modifier,
    trackHeight: Dp = 12.dp,
    iconSize: Dp = 80.dp,     // make the bike icon large
    containerHeight: Dp = 120.dp  // enough vertical space
) {
    // Calculate progress fraction (0..1)
    val fraction = if (totalDistance > 0) {
        (currentDistance / totalDistance).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    // We use BoxWithConstraints to measure available width/height in Dp.
    BoxWithConstraints(
        modifier = modifier
            .size(width = Dp.Unspecified, height = containerHeight) // fix the height
            .clipToBounds()  // ensure we don’t clip the icon if it extends
    ) {
        // Convert Dp to pixels
        val density = LocalDensity.current
        val containerWidthPx = with(density) { maxWidth.toPx() }
        val containerHeightPx = with(density) { maxHeight.toPx() }
        val trackHeightPx = with(density) { trackHeight.toPx() }
        val iconSizePx = with(density) { iconSize.toPx() }

        // We place the line near the bottom. Let's leave some padding (say 8px) from the bottom.
        val bottomPaddingPx = 8f
        // So the line’s vertical position (y) is:
        val lineY = containerHeightPx - trackHeightPx - bottomPaddingPx

        // We also want to pad the line horizontally by half the icon size,
        // so the icon doesn’t go off-screen on either side.
        val horizontalPaddingPx = iconSizePx / 2
        val trackLeftX = horizontalPaddingPx
        val trackRightX = containerWidthPx - horizontalPaddingPx
        val trackWidthPx = trackRightX - trackLeftX

        // The icon’s horizontal offset is fraction * trackWidthPx
        val iconX = trackLeftX + trackWidthPx * fraction
        // We want the icon’s bottom to align with lineY
        val iconY = lineY - iconSizePx

        // 1) Draw the track
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Gray full track
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

        // 2) Large bike icon, anchored so its bottom touches the line
        Icon(
            imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
            contentDescription = "Trip Progress",
            tint = Color(0xFF05C7BE),
            modifier = Modifier
                .align(Alignment.TopStart)
                // Convert float offsets to IntOffset for .offset()
                .offset {
                    IntOffset(
                        x = iconX.roundToInt() - (iconSizePx / 2).roundToInt(), // center icon horizontally
                        y = iconY.roundToInt()
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
