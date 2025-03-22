package com.ylabz.basepro.applications.bike.ui.components.home.dials

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.ui.components.path.DigitalCompassCard
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SpeedometerWithCompassOverlay(
    modifier: Modifier = Modifier,
    currentSpeed: Float,
    heading: Float,
    maxSpeed: Float = 60f,
) {
    Box(modifier = modifier) {
        // 1) The main speedometer
        FancySpeedometer(
            currentSpeed = currentSpeed,
            maxSpeed = maxSpeed,
            modifier = Modifier.fillMaxSize()
        )

        // 2) Overlay the digital compass at the bottom center
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                // Adjust negative offset to nudge it upwards between 0 and 60
                .offset(y = (-24).dp)
        ) {
            DigitalCompassCard(
                headingDegrees = heading,
                modifier = Modifier
                    .size(width = 80.dp, height = 60.dp)
            )
        }
    }
}

@Preview
@Composable
fun SpeedometerWithCompassOverlayPreview() {
    SpeedometerWithCompassOverlay(
        currentSpeed = 30f,
        heading = 45f,
        maxSpeed = 100f
    )
}
