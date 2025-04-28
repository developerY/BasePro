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
import com.ylabz.basepro.core.model.bike.RideState

@Composable
fun BigBikeProgressIndicator(
    modifier: Modifier = Modifier,
    currentDistance: Float,
    totalDistance: Float?,
    rideState: RideState,
    trackHeight: Dp = 8.dp,
    iconSize: Dp = 48.dp,
    iconTint: Color = Color.Gray,
    containerHeight: Dp = 70.dp,    // room for half the icon
    onBikeClick: () -> Unit
) {
    // 1) Compute fraction:
    //  - if we have a real totalDistance → 0..1 = current/total
    //  - else if ride hasn't started yet → .5 (center it)
    //  - else → 0 (start pos)
    val rawFraction = totalDistance
        ?.takeIf { it > 0f }
        ?.let { (currentDistance / it).coerceIn(0f, 1f) }
    val fraction = when {
        rawFraction != null -> rawFraction
        rideState == RideState.NotStarted -> 0.5f
        else -> 0f
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(containerHeight)
    ) {
        val bc = this
        val dens            = LocalDensity.current
        val wPx             = with(dens) { bc.maxWidth.toPx() }
        val hPx             = with(dens) { bc.maxHeight.toPx() }
        val trackHpx        = with(dens) { trackHeight.toPx() }
        val iconPx          = with(dens) { iconSize.toPx() }

        val lineY           = hPx / 2
        val padX            = iconPx / 2
        val leftX           = padX
        val rightX          = wPx - padX
        val trackW          = rightX - leftX

        val bikeCenterX     = leftX + trackW * fraction
        val bikeTopY        = lineY - iconPx / 2

        // 2) Always draw the base track:
        Canvas(Modifier.matchParentSize()) {
            drawLine(
                color       = Color.LightGray,
                start       = Offset(leftX, lineY),
                end         = Offset(rightX, lineY),
                strokeWidth = trackHpx
            )
            // And only draw the progress portion if we have a true totalDistance
            if (rawFraction != null) {
                drawLine(
                    color       = Color(0xFF90CAF9),
                    start       = Offset(leftX, lineY),
                    end         = Offset(leftX + trackW * rawFraction, lineY),
                    strokeWidth = trackHpx
                )
            }
        }

        // 3) Only show “0 / mid / full” markers when we know totalDistance
        if (rawFraction != null) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = iconSize / 2)
                    .offset(y = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0 km", style = MaterialTheme.typography.bodySmall)
                Text("${(totalDistance!! / 2).toInt()} km", style = MaterialTheme.typography.bodySmall)
                Text("${totalDistance.toInt()} km", style = MaterialTheme.typography.bodySmall)
            }
        }

        // 4) Finally, the bike icon itself, tappable
        Icon(
            imageVector        = Icons.AutoMirrored.Filled.DirectionsBike,
            contentDescription = "Trip Progress",
            tint               = iconTint,
            modifier           = Modifier
                .align(Alignment.TopStart)
                .offset {
                    IntOffset(
                        x = (bikeCenterX - iconPx / 2).roundToInt(),
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
            rideState       = RideState.NotStarted,
            onBikeClick     = { /* opens dialog */ }
        )
        Spacer(Modifier.height(24.dp))

        // 2) with a 10 km plan, at 2.5 km ridden
        BigBikeProgressIndicator(
            currentDistance = 2_500f,
            totalDistance   = 10_000f,
            rideState       = RideState.NotStarted,
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
            rideState       = RideState.NotStarted,
            onBikeClick     = { total = 10000f }
        )
        Spacer(Modifier.height(16.dp))
        current = 5000f
        total   = 10000f
        BigBikeProgressIndicator(
            currentDistance = current,
            totalDistance   = total,
            rideState       = RideState.Riding,
            iconTint        = Color.Green,
            onBikeClick     = { /* edit */ }
        )
    }
}
