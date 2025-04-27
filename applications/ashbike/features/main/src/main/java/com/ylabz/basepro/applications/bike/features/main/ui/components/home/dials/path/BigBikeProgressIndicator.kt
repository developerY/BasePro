package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path

import android.R.attr.iconTint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent

@Composable
fun BigBikeProgressIndicator(
    currentDistance: Float,
    totalDistance: Float?,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 8.dp,
    iconSize: Dp = 48.dp,
    iconTint: Color = Color.Gray,
    containerHeight: Dp = 70.dp,    // room for half-icon above/below track
    onBikeClick: () -> Unit
) {
    // 0f–1f fraction (0 if totalDistance is null or ≤0)
    val fraction: Float = totalDistance
        ?.takeIf { it > 0f }
        ?.let { (currentDistance / it).coerceIn(0f, 1f) }
        ?: 0f

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(containerHeight)
    ) {
        val bc = this
        val density = LocalDensity.current
        val containerWidthPx  = with(density) { bc.maxWidth.toPx() }
        val containerHeightPx = with(density) { bc.maxHeight.toPx() }
        val trackHeightPx     = with(density) { trackHeight.toPx() }
        val iconSizePx        = with(density) { iconSize.toPx() }

        // vertical center of the track
        val lineY = containerHeightPx / 2

        // leave half-icon padding on each side
        val padPx  = iconSizePx / 2
        val leftX  = padPx
        val rightX = containerWidthPx - padPx
        val trackW = rightX - leftX

        // bike’s center position on the track:
        val bikeCenterX = leftX + trackW * fraction
        val bikeTopY    = lineY - iconSizePx / 2

        // — only draw track & markers if totalDistance != null —
        if (totalDistance != null) {
            // 1) track & progress bar
            Canvas(Modifier.matchParentSize()) {
                // background
                drawLine(
                    color       = Color.LightGray,
                    start       = Offset(leftX, lineY),
                    end         = Offset(rightX, lineY),
                    strokeWidth = trackHeightPx
                )
                // progress
                drawLine(
                    color       = Color(0xFF90CAF9),
                    start       = Offset(leftX, lineY),
                    end         = Offset(leftX + trackW * fraction, lineY),
                    strokeWidth = trackHeightPx
                )
            }

            // 2) distance markers (“0 km”, “half”, “full”)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = iconSize / 2)  // match Canvas padding
                    .offset(y = 4.dp),                   // lift slightly off the edge
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0 km", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "${(totalDistance / 2).toInt()} km",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${totalDistance.toInt()} km",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // — always draw the bike icon —
        Icon(
            imageVector        = Icons.AutoMirrored.Filled.DirectionsBike,
            contentDescription = "Trip Progress",
            tint               = iconTint,
            modifier           = Modifier
                .align(Alignment.TopStart)
                .offset {
                    IntOffset(
                        x = (bikeCenterX - iconSizePx / 2).roundToInt(),
                        y = bikeTopY.roundToInt()
                    )
                }
                .size(iconSize)
                .clickable { onBikeClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BigBikeProgressIndicatorPreview() {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1) initial: no totalDistance → only centered bike
        BigBikeProgressIndicator(
            currentDistance = 0f,
            totalDistance   = null,
            iconTint        = Color.DarkGray,
            onBikeClick     = { /* opens dialog */ }
        )
        Spacer(Modifier.height(24.dp))

        // 2) with a 10 km plan, at 2.5 km ridden
        BigBikeProgressIndicator(
            currentDistance = 2_500f,
            totalDistance   = 10_000f,
            iconTint        = Color(0xFF4CAF50),
            onBikeClick     = { /* edit distance */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BigBikeProgressIndicatorPreviewOld() {
    var total by remember { mutableStateOf<Float?>(null) }
    var current by remember { mutableStateOf(0f) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BigBikeProgressIndicator(
            currentDistance = current,
            totalDistance   = total,
            iconTint        = Color.DarkGray,
            onBikeClick     = { total = 10000f }
        )
        Spacer(Modifier.height(16.dp))
        current = 5000f
        total   = 10000f
        BigBikeProgressIndicator(
            currentDistance = current,
            totalDistance   = total,
            iconTint        = Color.Green,
            onBikeClick     = { /* edit */ }
        )
    }
}
