package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import kotlin.math.roundToInt
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BigBikeProgressIndicator(
    modifier: Modifier = Modifier,
    currentDistance: Float,
    totalDistance: Float?,
    trackHeight: Dp = 8.dp,
    iconSize: Dp = 48.dp,
    iconTint: Color = Color.Gray,
    containerHeight: Dp = 70.dp,
) {
    val fraction: Float = totalDistance
        ?.takeIf { it > 0f }
        ?.let { (currentDistance / it).coerceIn(0f, 1f) }
        ?: 0f

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(containerHeight)
    ) {
        val boxWithConstraints  = this
        // Convert DP to pixels for precise offsets
        val density = LocalDensity.current
        val containerWidthPx  = with(density) { boxWithConstraints.maxWidth.toPx() }
        val containerHeightPx = with(density) { boxWithConstraints.maxHeight.toPx() }
        val trackHeightPx     = with(density) { trackHeight.toPx() }
        val iconSizePx        = with(density) { iconSize.toPx() }

        val lineY = containerHeightPx / 2
        val pad  = iconSizePx / 2
        val left = pad
        val right= containerWidthPx - pad
        val width= right - left

        val iconCenterX = left + width * fraction
        val iconTopY    = lineY - iconSizePx / 2

        // draw track
        Canvas(Modifier.matchParentSize()) {
            drawLine(
                color       = Color.LightGray,
                start       = Offset(left, lineY),
                end         = Offset(right, lineY),
                strokeWidth = trackHeightPx
            )
            drawLine(
                color       = Color(0xFF90CAF9),
                start       = Offset(left, lineY),
                end         = Offset(left + width * fraction, lineY),
                strokeWidth = trackHeightPx
            )
        }

        // bike icon, tappable with default ripple
        Icon(
            imageVector        = Icons.AutoMirrored.Filled.DirectionsBike,
            contentDescription = "Trip Progress",
            tint               = iconTint,
            modifier           = Modifier
                .align(Alignment.TopStart)
                .offset {
                    IntOffset(
                        x = (iconCenterX - iconSizePx/2).roundToInt(),
                        y = iconTopY.roundToInt()
                    )
                }
                .size(iconSize)
                //.clickable { onBikeClick() }  // ← default, non-deprecated ripple
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BigBikeProgressIndicatorPreview() {
    var total by remember { mutableStateOf<Float?>(null) }
    var current by remember { mutableStateOf(0f) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BigBikeProgressIndicator(
            currentDistance = current,
            totalDistance   = total,
            iconTint        = Color.DarkGray,
            //onBikeClick     = { total = 10000f }
        )
        Spacer(Modifier.height(16.dp))
        current = 5000f
        total   = 10000f
        BigBikeProgressIndicator(
            currentDistance = current,
            totalDistance   = total,
            iconTint        = Color.Green,
            //onBikeClick     = { /* edit */ }
        )
    }
}
