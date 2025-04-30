package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path


import android.R.attr.repeatMode
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.bike.RideState


@Composable
fun StartPauseButton(
    rideState: RideState,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPaused = true // rideState == RideState.Paused

    // Infinite transition for flashing when paused
    val infiniteTransition = rememberInfiniteTransition()
    val alpha = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    ).value.takeIf { isPaused } ?: 1f

    FloatingActionButton(
        onClick        = onToggle,
        containerColor = Color.White,
        contentColor   = Color.Black,
        modifier       = modifier
            .size(60.dp)
            .alpha(alpha)   // apply the flashing alpha only when paused
    ) {
        Icon(
            imageVector        = if (isPaused) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPaused) "Resume ride" else "Pause ride"
        )
    }
}



@Preview
@Composable
fun StartPauseButtonPreviewNotStarted() {
    StartPauseButton(
        rideState = RideState.NotStarted,

        onToggle = { /* Do nothing for preview */ }
    )
}

@Preview
@Composable
fun StartPauseButtonPreviewRiding() {
    StartPauseButton(
        rideState = RideState.Ended,
        onToggle = { /* Do nothing for preview */ }
    )
}




